package com.analisys.gimnasio.clases_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(bearerTokenRelayInterceptor());
        return restTemplate;
    }

    @Bean
    public ClientHttpRequestInterceptor bearerTokenRelayInterceptor() {
        return (request, body, execution) -> {
            String existingAuthHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (existingAuthHeader == null || existingAuthHeader.isBlank()) {
                String tokenValue = resolveJwtTokenValue();
                if (tokenValue != null && !tokenValue.isBlank()) {
                    request.getHeaders().setBearerAuth(tokenValue);
                }
            }
            return execution.execute(request, body);
        };
    }

    private static String resolveJwtTokenValue() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return null;
    }
}
