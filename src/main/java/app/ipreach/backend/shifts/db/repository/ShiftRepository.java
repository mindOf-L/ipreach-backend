package app.ipreach.backend.shifts.db.repository;

import app.ipreach.backend.shifts.db.model.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByLocationId(long locationId);

    @Query(value = """
            SELECT s.*
            FROM shifts s
            WHERE
                s.location_id = :locationId
            AND (
                 COALESCE(:yearMonth, '') = '' OR
                 TO_CHAR(s.date_time_from, 'YYYY-MM') = :yearMonth
                )
            AND (
                 COALESCE(:date, '') = '' OR
                 s.date_time_from::timestamp::date = :date
                )
            """, nativeQuery = true)
    List<Shift> findFiltered(long locationId, String yearMonth, LocalDate date);

}
