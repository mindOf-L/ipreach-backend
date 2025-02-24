package app.ipreach.backend.db.repository;

import app.ipreach.backend.db.model.Token;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tokens t WHERE t.expires_at < now()", nativeQuery = true)
    void deleteAllExpired();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tokens t WHERE t.user_id = :userId and t.expires_at < now()", nativeQuery = true)
    void deleteAllByUserIdAndExpired(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE from Token t WHERE t.userId = :userId and t.expiresAt < current_timestamp")
    void deleteAllExpiredTokensFromUser(Long userId);

    @Modifying
    @Transactional
    @Query("DELETE from Token t WHERE t.userId = :userId")
    void deleteAllTokensFromUser(Long userId);

    Optional<Token> findByJwtID(String jwtID);

    boolean existsByTokenHash(String tokenHash);

    Optional<Token> findByTokenHash(String tokenHash);

}
