package br.com.jbank.core.modulos.usuarios.domain;

import br.com.jbank.core.infra.defense.encryption.SensitiveDataConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tb_users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    // Fort Knox: Encrypted at Rest (AES-256)
    @Column(unique = true, nullable = false)
    @Convert(converter = SensitiveDataConverter.class)
    private String email;

    @Column(nullable = false)
    private String password;

    // Fort Knox: Transactional PIN
    @Column(name = "transaction_pin")
    private String transactionPin;

    public void validateTransactionPin(String rawPin, org.springframework.security.crypto.password.PasswordEncoder passwordEncoder) {
        if (this.transactionPin == null || !passwordEncoder.matches(rawPin, this.transactionPin)) {
             throw new SecurityException("PIN Transacional Inválido");
        }
    }

    @Builder.Default
    private String role = "ROLE_USER";

    // --- UserDetails Implementation ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if ("ROLE_ADMIN".equals(role)) {
            return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
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
}
