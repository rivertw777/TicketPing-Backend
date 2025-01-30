package com.ticketPing.user.application.dto;
import com.ticketPing.user.domain.model.entity.User;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.UUID;

@Builder(access = AccessLevel.PRIVATE)
public record UserResponse(
        UUID userId,
        String email,
        String nickname
) {
    public static UserResponse of(User user) {
        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
