package br.com.jbank.core.modulos.usuarios.controller;

import br.com.jbank.core.infra.response.JSendSuccessResponse;
import br.com.jbank.core.modulos.usuarios.domain.User;
import br.com.jbank.core.modulos.usuarios.domain.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@Slf4j
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record SetPinRequest(
        @Pattern(regexp = "\\d{4}", message = "PIN must be 4 digits") 
        String pin
    ) {}

    @PostMapping("/pin")
    @Operation(summary = "Set or Update Transaction PIN")
    public ResponseEntity<JSendSuccessResponse<Void>> setPin(@RequestBody @Valid SetPinRequest request, @AuthenticationPrincipal User user) {
        log.info("Atualizando PIN para usuário: {}", user.getEmail());
        
        // Load fresh user to ensure attached state (if needed) or simple save
        // In clean architecture, should use a Service, but simple repo save for MVP is fine
        user.setTransactionPin(passwordEncoder.encode(request.pin()));
        userRepository.save(user); // JPA merge
        
        return ResponseEntity.ok(JSendSuccessResponse.empty());
    }

}
