package app.ipreach.backend.core.security.user;

import app.ipreach.backend.shared.enums.ERole;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class UserDetailsImpl extends User implements UserDetails {

    private final Long id;

    public UserDetailsImpl(UserDetails userDetails, Long id) {
        super(userDetails.getUsername(), userDetails.getPassword(), userDetails.isEnabled(), userDetails.isAccountNonExpired(), userDetails.isCredentialsNonExpired(), userDetails.isAccountNonExpired(), userDetails.getAuthorities());
        this.id = id;
    }

    public boolean equals(Object o) {
        return o instanceof UserDetailsImpl && this.id.equals(((UserDetailsImpl) o).getId());
    }

    public ERole[] getRolesFromAuthorities() {
        return this.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .map(ERole::getEnumFromAuthority)
            .toArray(ERole[]::new);
    }

}
