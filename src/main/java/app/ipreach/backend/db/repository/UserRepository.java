package app.ipreach.backend.db.repository;

import app.ipreach.backend.db.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
