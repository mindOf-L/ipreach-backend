package app.ipreach.backend.db.model;

import app.ipreach.backend.shared.constants.ERole;
import app.ipreach.backend.shared.conversion.ERoleConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Long id;

    private Long botId;

    private String botName;

    private String botPhone;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String phone2;

    private String congregation;

    @NonNull @NotNull @Column(nullable = false)
    @Convert(converter = ERoleConverter.class)
    private List<ERole> roles;

    private boolean approved;

}
