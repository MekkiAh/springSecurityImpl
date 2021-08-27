package spring.security.impl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.security.impl.dto.LoginResponse;
import spring.security.impl.dto.TokenRefreshResponseDTO;
import spring.security.impl.dto.TokenResreshRequestDTO;
import spring.security.impl.dto.UserLoginDTO;
import spring.security.impl.entities.RefreshToken;
import spring.security.impl.entities.User;
import spring.security.impl.repositories.RefreshTokenRepository;
import spring.security.impl.repositories.UserRepository;
import spring.security.impl.utils.JwtUtils;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder ;
    @Autowired
    AuthenticationManager authenticationManager ;
    @Autowired
    JwtUtils jwtUtils ;
    @Autowired
    RefreshTokenRepository refreshTokenRepository ;
    public User getUserById(String id){
        Optional<User> user = userRepository.findById(id);
        if (!user.isPresent())
            throw new UsernameNotFoundException("User not found");
        return user.get() ;
    }

    public ResponseEntity<?> signUp(User user) throws Exception {
        Optional<User> userOptional = userRepository.findUserByUsername(user.getUsername());
        if(userOptional.isPresent())
            throw new Exception("User  exists");
        userOptional = userRepository.findUserByEmail(user.getEmail());
        if (userOptional.isPresent())
            throw new Exception("User exists");
        String hashedPassword ;
        hashedPassword= bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
        return new ResponseEntity<String>("user added", HttpStatus.ACCEPTED);

    }

    public ResponseEntity<?> login(UserLoginDTO userLoginDTO){
        Optional<User> userOptional = userRepository.findUserByUsername(userLoginDTO.getUsername());
        if(!userOptional.isPresent())
            throw new UsernameNotFoundException("user doesn't exist");
        try{
            authenticationManager.
                    authenticate(new UsernamePasswordAuthenticationToken
                            (userLoginDTO.getUsername(),
                            userLoginDTO.getPassword(),
                            new ArrayList<>())
                    );
        }
        catch (Exception e)
        {
            return new ResponseEntity<>(e,HttpStatus.UNAUTHORIZED);
        }
        Map<String,Object> claims = new HashMap<>();
        claims.put("name",userOptional.get().getFirstName()+userOptional.get().getLastName());
        claims.put("username",userOptional.get().getUsername());
        claims.put("email",userOptional.get().getEmail());
        claims.put("authorities",new ArrayList<>());

        String accessTokenJwt = jwtUtils.createAccessToken(claims,userOptional.get().getId());
        String refreshTokenJwt = jwtUtils.createRefreshToken(claims,userOptional.get().getId());
        refreshTokenRepository.save(new RefreshToken(0,refreshTokenJwt));


        return new ResponseEntity<>(new LoginResponse(accessTokenJwt,refreshTokenJwt,userOptional.get()),HttpStatus.ACCEPTED);
    }
    @Transactional
    public ResponseEntity<?> refreshToken(TokenResreshRequestDTO tokenResreshRequestDTO)
    {
        UserDetails userDetails= new org.springframework.security.core.userdetails.User
                (jwtUtils.getUserIdFromToken(tokenResreshRequestDTO.getRefreshToken()),
                        "",
                        (Collection<? extends GrantedAuthority>) jwtUtils.getAuthoritiesFromToken(tokenResreshRequestDTO.getRefreshToken()));
        if(refreshTokenRepository.findRefreshTokenByRefreshToken(tokenResreshRequestDTO.getRefreshToken()).isPresent())
        {
            if (jwtUtils.tokenIsValid(tokenResreshRequestDTO.getRefreshToken(), userDetails)) {
                Map<String, Object> claims = new HashMap<>();
                Optional<User> user = userRepository.findById(userDetails.getUsername());
                claims.put("name", user.get().getFirstName() + user.get().getLastName());
                claims.put("username", user.get().getUsername());
                claims.put("email", user.get().getEmail());
                claims.put("authorities", userDetails.getAuthorities());
                String accessTokenJwt = jwtUtils.createAccessToken(claims, user.get().getId());
                String refreshTokenJwt = jwtUtils.createRefreshToken(claims, user.get().getId());
                refreshTokenRepository.save(new RefreshToken(0,refreshTokenJwt));
                refreshTokenRepository.deleteByRefreshToken(tokenResreshRequestDTO.getRefreshToken());
                return new ResponseEntity<>(new TokenRefreshResponseDTO(accessTokenJwt,refreshTokenJwt), HttpStatus.OK);

            }
            return new ResponseEntity<>("refresh token invalid", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>("refresh token doesn't exist", HttpStatus.UNAUTHORIZED);

    }
}
