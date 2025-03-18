package app.ipreach.backend.shifts.db.model;

import app.ipreach.backend.locations.db.model.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @Transient
    private String locationName;

    @Column(nullable = false)
    private LocalDateTime dateTimeFrom;

    @Column(nullable = false)
    private LocalDateTime dateTimeTo;

    @Column(nullable = false)
    private int slotsOpened;

    @Column(nullable = false)
    private int slotsAvailable;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_assignment_id")
    private List<ShiftAssignment> assignments;

    public String getLocationName() {
        if(StringUtils.isBlank(locationName))
            locationName = location.getName();

        return locationName;
    }

}
