package app.ipreach.backend.locations.db.repository;

import app.ipreach.backend.locations.db.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    boolean existsById(Long id);

}
