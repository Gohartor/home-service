package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;
import org.example.entity.enumerator.ServiceStatus;

import java.time.ZonedDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    private String description;

    private Double offeredPrice;

    private Double totalPrice;

    private String address;

    private boolean paid;

    private ZonedDateTime expectedDoneAt;

    private ZonedDateTime doneAt;

    @ManyToOne
    private User customer;

    @ManyToOne
    private User expert;

    @ManyToOne
    private Service service;

    @Enumerated(EnumType.STRING)
    private ServiceStatus status;

}