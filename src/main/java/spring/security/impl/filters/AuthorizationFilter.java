package spring.security.impl.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.security.impl.utils.JwtUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@Component
public class AuthorizationFilter  extends OncePerRequestFilter {
    @Autowired
    JwtUtils jwtUtils ;
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String bearertoken = httpServletRequest.getHeader("Authorization");
        String jwt ;
        String id ;
        if(bearertoken!= null && bearertoken.startsWith("Bearer ") )
        {
            System.out.println("-1");

            try
            {

                jwt=bearertoken.substring(7);
                id = jwtUtils.getUserIdFromToken(jwt);
                System.out.println("0 "+id);

                UserDetails userDetails = new User(id,"",(Collection<? extends GrantedAuthority>) jwtUtils.getAuthoritiesFromToken(jwt));
                if(jwtUtils.tokenIsValid(jwt,userDetails))
                {
                    System.out.println("2");

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());;
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
                filterChain.doFilter(httpServletRequest,httpServletResponse);
            }
            catch (Exception e)
            {

                httpServletResponse.setHeader("error-message",e.getMessage());
                httpServletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
            }

        }
        else {
            System.out.println("4");

            filterChain.doFilter(httpServletRequest,httpServletResponse);

        }

    }
}
