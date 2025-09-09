package com.pitchplease.userservice.model.user.entity;

import com.pitchplease.userservice.model.common.entity.BaseEntity;
import com.pitchplease.userservice.model.user.enums.TokenClaims;
import com.pitchplease.userservice.model.user.enums.UserStatus;
import com.pitchplease.userservice.model.user.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a user entity named {@link UserEntity} in the system.
 * This entity stores user-related information such as email, password, and personal details.
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String password;

    @Column(name = "username")
    private String userName;

    // @Column(name = "LAST_NAME")
    // private String lastName;

    // @Column(
    //         name = "PHONE_NUMBER",
    //         length = 20
    // )
    // private String phoneNumber;


    // @Enumerated(EnumType.STRING)
    // private UserType userType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    /**
     * Constructs a map of claims based on the user's attributes.
     * This map is typically used to create JWT claims for the user.
     * @return a map of claims containing user attributes
     */
    public Map<String, Object> getClaims() {

        final Map<String, Object> claims = new HashMap<>();

        claims.put(TokenClaims.USER_ID.getValue(), this.id);
        // claims.put(TokenClaims.USER_TYPE.getValue(), this.userType);
        claims.put(TokenClaims.USER_STATUS.getValue(), this.userStatus);
        claims.put(TokenClaims.USER_NAME.getValue(), this.userName);
        // claims.put(TokenClaims.USER_LAST_NAME.getValue(), this.lastName);
        claims.put(TokenClaims.USER_EMAIL.getValue(), this.email);
        // claims.put(TokenClaims.USER_PHONE_NUMBER.getValue(), this.phoneNumber);

        return claims;

    }

}
