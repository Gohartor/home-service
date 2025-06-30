package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double offeredPrice;

    @Column
    private ZonedDateTime startAt;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Proposal> proposals;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Review review;
}