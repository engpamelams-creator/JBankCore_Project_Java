package br.com.jbank.core.modulos.pix.service;

import br.com.jbank.core.modulos.pix.domain.PixKey;
import br.com.jbank.core.modulos.pix.domain.PixKeyRepository;
import br.com.jbank.core.modulos.pix.dto.PixDTOs;
import br.com.jbank.core.modulos.usuarios.domain.User;
import br.com.jbank.core.modulos.usuarios.domain.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PixService {

    private final PixKeyRepository pixKeyRepository;
    private final UserRepository userRepository;

    @Autowired
    public PixService(PixKeyRepository pixKeyRepository, UserRepository userRepository) {
        this.pixKeyRepository = pixKeyRepository;
        this.userRepository = userRepository;
    }

    public PixDTOs.PixKeyResponse createKey(PixDTOs.PixKeyRequest request) {
        User user = getAuthenticatedUser();
        log.info("Iniciando criação de chave Pix para usuário: {}", user.getEmail());

        // 1. Validação de Limite
        if (pixKeyRepository.countByUserId(user.getId()) >= 5) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Pix key limit reached (Max 5)");
        }

        // 2. Validação de Unicidade
        if (pixKeyRepository.existsByKey(request.key())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pix key already exists");
        }

        // 3. Persistência
        PixKey pixKey = PixKey.builder()
                .user(user)
                .type(request.type())
                .key(request.key())
                .build();

        pixKeyRepository.save(pixKey);
        log.info("Chave Pix criada com sucesso: ID={}, User={}", pixKey.getId(), user.getEmail());

        return mapToResponse(pixKey);
    }

    public List<PixDTOs.PixKeyResponse> listMyKeys() {
        User user = getAuthenticatedUser();
        return pixKeyRepository.findAllByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void deleteKey(UUID keyId) {
        User user = getAuthenticatedUser();
        PixKey pixKey = pixKeyRepository.findById(keyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Key not found"));

        if (!pixKey.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        pixKeyRepository.delete(pixKey);
        log.info("Chave Pix deletada: ID={}", keyId);
    }

    private User getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid authentication");
    }

    private PixDTOs.PixKeyResponse mapToResponse(PixKey pixKey) {
        return new PixDTOs.PixKeyResponse(
                pixKey.getId(),
                pixKey.getKey(),
                pixKey.getType(),
                pixKey.isActive(),
                pixKey.getCreatedAt()
        );
    }
}
