package com.ticketPing.user.domain.repository;

import com.ticketPing.user.domain.model.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID userId);

    boolean existsByEmail(String email);
}
