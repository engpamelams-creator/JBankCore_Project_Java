package br.com.jbank.core.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI/Swagger Configuration for JBank Core API.
 * 
 * Provides interactive API documentation with JWT authentication support.
 * 
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 * 
 * @author Pamela Menezes
 */
@Configuration
public class OpenApiConfig {
    
    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, createSecurityScheme()));
    }
    
    private Info apiInfo() {
        return new Info()
                .title("JBank Core API")
                .version("1.0.0")
                .description("""
                    # JBank Core - Enterprise Fintech API
                    
                    ## 🛡️ Security Features
                    - **JWT Authentication**: Secure token-based authentication
                    - **Rate Limiting**: Bucket4j for DDoS protection
                    - **AES-256 Encryption**: PII data encryption at rest
                    - **Transactional PIN**: Secondary authentication for transfers
                    
                    ## 🏗️ Architecture
                    - **Clean Architecture**: Domain-Driven Design (DDD)
                    - **Modular Monolith**: Ready for microservices
                    - **Event-Driven**: RabbitMQ integration
                    - **ACID Transactions**: Pessimistic locking for consistency
                    
                    ## 📊 Core Features
                    - User Management & Authentication
                    - Wallet Management with ACID transactions
                    - PIX Key Registration & Management
                    - Money Transfers with concurrency control
                    
                    ## 🔐 How to Authenticate
                    1. Use `/auth/signup` to create an account
                    2. Use `/auth/login` to get your JWT token
                    3. Click the "Authorize" button above
                    4. Enter: `Bearer <your-token>`
                    5. Test protected endpoints!
                    """)
                .contact(new Contact()
                        .name("Pamela Menezes")
                        .email("pamela@jbank.com")
                        .url("https://github.com/engpamelams-creator/JBankCore_Project_Java"))
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT"));
    }
    
    private SecurityScheme createSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter your JWT token obtained from /auth/login endpoint");
    }
}
