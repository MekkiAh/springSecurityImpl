package spring.security.impl.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import spring.security.impl.entities.User;
import spring.security.impl.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Optional;

@Component
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository ;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(s);
        if (!user.isPresent())
            throw new UsernameNotFoundException("User not found");
        return new org.springframework.security.core.userdetails.User(user.get().getId(),user.get().getPassword(),new ArrayList<>());
    }
}
