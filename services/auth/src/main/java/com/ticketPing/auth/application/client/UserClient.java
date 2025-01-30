package com.ticketPing.auth.application.client;

import org.springframework.http.ResponseEntity;
import response.CommonResponse;
import user.UserLookupRequest;
import user.UserResponse;

public interface UserClient {
    ResponseEntity<CommonResponse<UserResponse>> getUserByEmailAndPassword(UserLookupRequest userLookupRequest);
}
