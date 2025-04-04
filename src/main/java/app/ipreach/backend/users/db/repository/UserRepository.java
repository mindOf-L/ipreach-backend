package app.ipreach.backend.users.db.repository;

import app.ipreach.backend.users.db.model.User;
import app.ipreach.backend.users.payload.dto.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
        SELECT u
        FROM User u
        WHERE u.email = :email
        """)
    Optional<User> findByEmail(String email);

    @Query(value = """
        SELECT u.id, u.email, u.roles
        FROM users u
        WHERE u.email = :email
        """, nativeQuery = true)
    UserDto userDetailsByEmail(String email);

    @Query("""
        SELECT u FROM User u
        ORDER BY RANDOM()
        LIMIT :participants
        """)
    List<User> giveMeRandomParticipants(int participants);

    @Query(value = """
            SELECT COUNT(u.*) >= 1
            FROM users u
            WHERE
                CASE
                   WHEN :email IS NOT NULL
                   THEN u.email = :email
                   ELSE FALSE
                END
            OR
                CASE
                   WHEN :phone IS NOT NULL
                   THEN u.phone = :phone
                   ELSE FALSE
                END
            """, nativeQuery = true)
    Boolean existsByEmailOrPhone(String email, String phone);

}
