package app.ipreach.backend.app.security.user;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Builder(toBuilder = true)
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final boolean emailVerified;
    private final Collection<? extends GrantedAuthority> authorities;
    private LocalDateTime createdAt;
    private LocalDateTime signedTerms;
    private Boolean needsAcceptTerms;

    public String getUsername() {
        return email;
    }

}

