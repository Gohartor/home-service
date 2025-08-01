package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;

import java.time.ZonedDateTime;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class EmailVerificationToken extends BaseEntity {

    private String token;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private ZonedDateTime expiresAt;

    private boolean isUsed;

}
