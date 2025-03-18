package app.ipreach.backend.shifts.db.repository;

import app.ipreach.backend.shifts.db.model.ShiftRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRequestRepository extends JpaRepository<ShiftRequest, Long> { }
