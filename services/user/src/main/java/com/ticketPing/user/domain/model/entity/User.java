package com.ticketPing.user.domain.model.entity;

import audit.BaseEntity;
import com.ticketPing.user.presentation.request.CreateUserRequest;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "p_user")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID id;

    private String email;
    private String password;
    private String nickname;

    public static User from(CreateUserRequest request, String encodedPassword) {
        return User.builder()
                .email(request.email())
                .password(encodedPassword)
                .nickname(request.nickname())
                .build();
    }
}
