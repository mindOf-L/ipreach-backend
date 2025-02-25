package app.ipreach.backend.users.db.model;

import app.ipreach.backend.shared.conversion.ERoleConverter;
import app.ipreach.backend.shared.enums.ERole;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    private String email;

    private String password;

    private Long botId;

    private String botName;

    private String botPhone;

    @Column(nullable = false)
    private String name;

    private String phone;

    private String phone2;

    private String congregation;

    @NotNull @Column(nullable = false)
    @Convert(converter = ERoleConverter.class)
    private List<ERole> roles;

    public String[] getRolesUserDetails() {
        return this.getRoles().stream().map(ERole::getRoleName).toArray(String[]::new);
    }

    public List<SimpleGrantedAuthority> getRolesAuthorities() {
        return this.getRoles().stream()
            .map(r -> new SimpleGrantedAuthority(r.getRoleName()))
            .toList();
    }

    @Column(nullable = false)
    private boolean approved;

    @Column(nullable = false)
    private boolean enabled;

}
