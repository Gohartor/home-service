package org.example.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSession extends BaseEntity {

    private String token;

    private LocalDateTime expireAt;
    private Boolean paid = false;

    private Long orderId;

    private String captchaCode;
}

