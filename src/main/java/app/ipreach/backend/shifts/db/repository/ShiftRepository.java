package app.ipreach.backend.shifts.db.repository;

import app.ipreach.backend.shifts.db.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftRepository extends JpaRepository<Shift, Long> { }
