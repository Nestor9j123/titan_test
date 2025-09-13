package nitchcorp.backend.titan.shared.securite.securiteconfig.UserDetailsconf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nitchcorp.backend.titan.shared.securite.user.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UUID trackingId;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private String roles;   // Rôle en String
    private boolean actif;  // état actif du user

    // Constructeur principal
    public UserDetailsImpl(Long id,
                           String firstName,
                           String lastName,
                           String email,
                           String password,
                           String phone,
                           UUID trackingId,
                           Collection<? extends GrantedAuthority> authorities,
                           String roles,
                           boolean actif) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.trackingId = trackingId;
        this.authorities = authorities;
        this.roles = roles;
        this.actif = actif;
    }

    // Méthode statique pour construire UserDetailsImpl depuis User
    public static UserDetailsImpl build(User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new UserDetailsImpl(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPassword(),
                user.getPhone(),
                user.getTrackingId(),
                authorities,
                user.getRole().name(),
                user.isActif()
        );
    }

    // -------------------- UserDetails interface --------------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // -------------------- Getters supplémentaires --------------------
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public UUID getTrackingId() {
        return trackingId;
    }

    public String getRoles() {
        return roles;
    }

    public boolean isActif() {
        return actif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserDetailsImpl))
            return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
