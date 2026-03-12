package com.analisys.gimnasio.clases_service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API.
 *
 * Acceder a la documentación:
 * - Swagger UI: http://localhost:8082/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8082/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Clases Service API")
                .version("1.0.0")
                .description("""
                    API REST para la gestión de clases del gimnasio.

                    ## Funcionalidades
                    - Programación y gestión de clases
                    - Integración con equipos (reservas / liberación)
                    - Integración con entrenadores (asignación / consulta)

                    ## Autenticación
                    Esta API utiliza OAuth2/JWT con Keycloak.
                    Para obtener un token, autentícate en Keycloak con las credenciales proporcionadas.

                    ## Roles
                    - **ADMIN**: Acceso completo
                    - **TRAINER**: Gestión/consulta de clases asignadas
                    - **MEMBER**: Consulta de clases
                    """)
                .contact(new Contact()
                    .name("Equipo Gimnasio")
                    .email("soporte@gimnasio.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8082")
                    .description("Servidor de desarrollo")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Token JWT obtenido de Keycloak"))
                .addSecuritySchemes("oauth2", new SecurityScheme()
                    .type(SecurityScheme.Type.OAUTH2)
                    .flows(new OAuthFlows()
                        .password(new OAuthFlow()
                            .tokenUrl(issuerUri + "/protocol/openid-connect/token")))));
    }
}
