package br.com.pamela.jbank.infra.security;

import br.com.pamela.jbank.modulos.usuarios.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Buscando usuário para autenticação: {}", email);

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Tentativa de login falhou. Email não encontrado: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });

        log.debug("Usuário encontrado: {}", user.getId());

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles("USER") // Simple role for MVP
                .build();
    }
}
