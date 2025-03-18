package app.ipreach.backend.shifts.db.repository;

import app.ipreach.backend.shifts.db.model.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftAssigmentRepository extends JpaRepository<ShiftAssignment, Long> { }
