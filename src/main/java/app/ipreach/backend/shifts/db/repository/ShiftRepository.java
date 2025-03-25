package app.ipreach.backend.shifts.db.repository;

import app.ipreach.backend.shifts.db.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByLocationId(long locationId);

    @Query("""
            SELECT s
            FROM Shift s
            WHERE
                s.location.id = :locationId
            AND (
                 COALESCE(:yearMonth, '') = '' OR
                 TO_CHAR(s.dateTimeFrom, 'YYYY-MM') = :yearMonth
                )
            AND (
                 COALESCE(:date, '') = '' OR
                 TO_CHAR(s.dateTimeFrom, 'YYYY-MM-DD') = :date
                )
            """)
    List<Shift> findFiltered(long locationId, String yearMonth, LocalDate date);

}
