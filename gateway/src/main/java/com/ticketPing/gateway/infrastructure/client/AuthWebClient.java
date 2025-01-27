package com.ticketPing.gateway.infrastructure.client;

import auth.UserCacheDto;
import com.ticketPing.gateway.application.client.AuthClient;
import com.ticketPing.gateway.common.exception.SecurityErrorCase;
import exception.ApplicationException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import response.CommonResponse;

@Component
public class AuthWebClient implements AuthClient {

    private final WebClient webClient;

    public AuthWebClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Mono<UserCacheDto> validateToken(String token) {
        return webClient.post()
                .uri("http://auth/api/v1/auth/validate")
                .header("Authorization", token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<CommonResponse<UserCacheDto>>() {})
                .flatMap(response -> {
                    if (response.getData() != null) {
                        return Mono.just(response.getData());
                    } else {
                        return Mono.error(new ApplicationException(SecurityErrorCase.UNAUTHORIZED));
                    }
                });
    }

}
