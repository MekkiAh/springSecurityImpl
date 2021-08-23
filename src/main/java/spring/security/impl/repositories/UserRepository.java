package spring.security.impl.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.security.impl.entities.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,String> {
        Optional<User> findUserByUsername (String username);
        Optional<User> findUserByEmail (String email);
}
