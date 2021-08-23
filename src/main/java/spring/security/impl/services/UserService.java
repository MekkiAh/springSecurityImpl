package spring.security.impl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import spring.security.impl.dto.LoginResponse;
import spring.security.impl.dto.UserLoginDTO;
import spring.security.impl.entities.User;
import spring.security.impl.repositories.UserRepository;
import spring.security.impl.utils.JwtUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        String jwt = jwtUtils.createAccessToken(claims,userOptional.get().getId());
        return new ResponseEntity<>(new LoginResponse(jwt,userOptional.get()),HttpStatus.ACCEPTED);
    }
}
