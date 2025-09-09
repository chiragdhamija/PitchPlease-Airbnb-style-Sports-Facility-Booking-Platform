package com.pitchplease.userservice.service;

import com.pitchplease.userservice.model.user.User;
import com.pitchplease.userservice.model.user.dto.request.RegisterRequest;

/**
 * Service interface named {@link RegisterService} for user registration operations.
 */
public interface RegisterService {

    /**
     * Registers a new user with the provided registration request.
     *
     * @param registerRequest the request containing user registration details.
     * @return the registered {@link User} instance.
     */
    User registerUser(final RegisterRequest registerRequest);

}
