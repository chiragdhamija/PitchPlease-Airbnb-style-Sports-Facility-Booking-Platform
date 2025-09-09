package com.pitchplease.userservice.model.user.entity;

import com.pitchplease.userservice.model.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Represents an entity named {@link InvalidTokenEntity} for storing invalid tokens in the system.
 * This entity tracks tokens that have been invalidated to prevent their reuse.
 */
@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "invalid_tokens")
public class InvalidTokenEntity extends BaseEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "token_id")
    private String tokenId;

}
