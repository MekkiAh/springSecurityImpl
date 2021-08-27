package spring.security.impl.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.security.impl.entities.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken , Integer> {
    void deleteByRefreshToken(String refreshToken);
    Optional<RefreshToken> findRefreshTokenByRefreshToken(String refreshToken);
}
