package spring.security.impl.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import spring.security.impl.entities.User;

import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtUtils {
    //5 minutes
    private static final long accessTokenValidity = 5 *60 * 1000;
    //1 year
    private static final long refreshTokenValidity = 12 * 30 * 24 * 60 *60 *1000 ;


    private static final String accessTokenPrivateKey="key9wi";
    private static final String refreshTokenPrivateKey="key9wi";
   public String createAccessToken(Map<String, Object> claims, String subject){
       String encodedKey = Base64.getEncoder().encodeToString(accessTokenPrivateKey.getBytes());
       return Jwts.builder()
               .setClaims(claims)
               .setSubject(subject)
               .setIssuedAt(new Date(System.currentTimeMillis()))
               .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
               .signWith(SignatureAlgorithm.HS512, encodedKey).compact();
   }
   public String createRefreshToken(Map<String, Object> claims, String subject){
        String encodedKey = Base64.getEncoder().encodeToString(refreshTokenPrivateKey.getBytes());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(SignatureAlgorithm.HS512, encodedKey).compact();
    }
    public boolean tokenIsExpired(String token){
        Date expirationDate = getClaimsFromToken(token).getExpiration();
        return (new Date().after(expirationDate));
    }
    public String getUserIdFromToken(String token){
        return  getClaimsFromToken(token).getSubject();
    }
    public List getAuthoritiesFromToken(String token)
    {

        System.out.println(getClaimsFromToken(token).get("authorities"));
        return (List) getClaimsFromToken(token).get("authorities");
    }
    public Claims getClaimsFromToken(String token)
    {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(Base64.getEncoder().encodeToString(accessTokenPrivateKey.getBytes())
)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public boolean tokenIsValid ( String token, UserDetails userDetails){
        final String userId = getUserIdFromToken(token);
        return (userId.equals(userDetails.getUsername()) && !tokenIsExpired(token));
    }
}
