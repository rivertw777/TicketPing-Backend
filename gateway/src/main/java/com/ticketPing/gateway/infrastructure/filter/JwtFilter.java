package com.ticketPing.gateway.infrastructure.filter;

import static com.ticketPing.gateway.common.exception.CircuitBreakerErrorCase.SERVICE_UNAVAILABLE;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketPing.gateway.application.client.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter implements ServerSecurityContextRepository {

    @Value("${client.url}")
    private String clientUrl;

    private final AuthClient authClient;

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return null;
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null) {
            return Mono.empty();
        }

        return authClient.validateToken(authHeader)
                .map(response -> {
                    List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(response.role()));
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            response.userId(), null, authorities
                    );

                    exchange.mutate()
                            .request(r -> r.headers(headers -> {
                                headers.add("X_USER_ID", String.valueOf(response.userId()));
                                headers.add("X_USER_ROLE", response.role());
                            }))
                            .build();

                    return new SecurityContextImpl(authentication);
                })
                .cast(SecurityContext.class)
                .onErrorResume(WebClientRequestException.class, e ->
                        setErrorResponse(exchange, HttpStatus.SERVICE_UNAVAILABLE, SERVICE_UNAVAILABLE.getMessage()))
                .onErrorResume(WebClientResponseException.Unauthorized.class, e ->
                    extractMessageFromResponse(e)
                            .flatMap(message -> setErrorResponse(exchange, HttpStatus.UNAUTHORIZED, message))
                );
    }

    private Mono<SecurityContext> setErrorResponse(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setAccessControlAllowOrigin(clientUrl);
        response.getHeaders().setAccessControlAllowCredentials(true);
        DataBuffer dataBuffer = response.bufferFactory().wrap(message.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(dataBuffer))
                .then(Mono.empty());
    }

    private Mono<String> extractMessageFromResponse(WebClientResponseException e) {
        String responseBody = e.getResponseBodyAsString();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);
            String message = jsonNode.path("message").asText("");
            return Mono.just(message);
        } catch (Exception ex) {
            return Mono.empty();
        }
    }

}