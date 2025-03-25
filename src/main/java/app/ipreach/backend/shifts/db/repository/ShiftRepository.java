package app.ipreach.backend.shifts.db.repository;

import app.ipreach.backend.shifts.db.model.Shift;
import app.ipreach.backend.shifts.payload.dto.ShiftSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShiftRepository extends JpaRepository<Shift, Long> {

    List<Shift> findByLocationId(long locationId);

    @Query(value = """
            SELECT s.*
            FROM shifts s
            WHERE
                s.location_id = :locationId
            AND
                CASE
                   WHEN :yearMonth IS NOT NULL
                   THEN TO_CHAR(s.date_time_from, 'YYYY-MM') = :yearMonth
                   ELSE TRUE
                END
            AND
                CASE
                   WHEN :localDate IS NOT NULL
                   THEN TO_CHAR(s.date_time_from, 'YYYY-MM-DD') = :localDate
                   ELSE TRUE
                END
            """, nativeQuery = true)
    List<Shift> findFiltered(long locationId, String yearMonth, String localDate);

    @Query(value = """
        SELECT
            cast(s.date_time_from as date) as shiftsDate,
            count(to_char(date_time_from, 'YYYY-MM-DD'))::int as shiftsRegistered,
            count(*) filter(where s.slots_available > 0)::int as shiftsAvailable
        FROM shifts s
        WHERE location_id = :locationId
          AND to_char(date_time_from, 'YYYY-MM') = :yearMonth
        GROUP BY shiftsDate
        ORDER BY shiftsDate
        """, nativeQuery = true)
    List<ShiftSummaryDto> findSummarized(String yearMonth, long locationId);

}
