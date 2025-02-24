package app.ipreach.backend.db.repository;

import app.ipreach.backend.db.model.User;
import app.ipreach.backend.mapper.dto.user.UserDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

}
