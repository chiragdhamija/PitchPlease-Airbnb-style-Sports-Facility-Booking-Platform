package com.pitchplease.userservice.service.impl;

import com.pitchplease.userservice.exception.PasswordNotValidException;
import com.pitchplease.userservice.exception.UserNotFoundException;
import com.pitchplease.userservice.model.user.Token;
import com.pitchplease.userservice.model.user.dto.request.LoginRequest;
import com.pitchplease.userservice.model.user.entity.UserEntity;
import com.pitchplease.userservice.repository.UserRepository;
import com.pitchplease.userservice.service.TokenService;
import com.pitchplease.userservice.service.UserLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link UserLoginService} for handling user login operations.
 * This service handles user authentication by validating login credentials and generating JWT tokens.
 */
@Service
@RequiredArgsConstructor
public class UserLoginServiceImpl implements UserLoginService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;

    /**
     * Authenticates a user based on the provided login request and generates a JWT token upon successful login.
     * This method retrieves the user entity from the database using the email provided in the login request.
     * It then validates the provided password against the stored password. If the credentials are valid,
     * it generates and returns a JWT token containing the user's claims.
     *
     * @param loginRequest the {@link LoginRequest} object containing the user's email and password.
     * @return a {@link Token} object containing the generated JWT token.
     * @throws UserNotFoundException if no user is found with the given email.
     * @throws PasswordNotValidException if the provided password does not match the stored password.
     */
    @Override
    public Token login(LoginRequest loginRequest) {

        final UserEntity userEntityFromDB = userRepository
                .findUserEntityByUserName(loginRequest.getUserName())
                .orElseThrow(
                        () -> new UserNotFoundException("Can't find with given email: "
                                + loginRequest.getUserName())
                );

        if (Boolean.FALSE.equals(passwordEncoder.matches(
                loginRequest.getPassword(), userEntityFromDB.getPassword()))) {
            throw new PasswordNotValidException();
        }

        return tokenService.generateToken(userEntityFromDB.getClaims());

    }

}
