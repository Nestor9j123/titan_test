package nitchcorp.backend.titan.shared.securite.user.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nitchcorp.backend.titan.shared.utils.BaseEntity;
import nitchcorp.backend.titan.shared.utils.constantSecurities.TypeRole;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name="USERS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "tracking_id" , nullable = false , unique = true)
    private UUID trackingId ;

    @Column(nullable = false, length = 50)
    private  String firstName ;

    @Column(nullable = false, length = 50)
    private  String lastName ;

    @Column(nullable = false, length = 15)
    private  String phone ;

    @Column(nullable = false , unique = true,length = 100)
    private  String email ;

    @Column(nullable = false,length = 100)
    private  String password ;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TypeRole role ;

    @Column(nullable = false)
    private boolean actif = false;

    @Column(length = 100)
    private String country;

    public User(String firstName, String lastName, String email, String password, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public User(Long id,
                String firstName,
                String lastName,
                String email,
                String password,
                String phone,
                UUID trackingId,
                TypeRole role,
                boolean actif) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.trackingId = trackingId;
        this.role = role;
        this.actif = actif;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("Role" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
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
}
