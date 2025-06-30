package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;
import java.time.ZonedDateTime;

@Entity
@Table(name = "proposals")
@Getter
@Setter
@NoArgsConstructor
public class Proposal extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    private User specialist;

    @Column(nullable = false)
    private Double proposedPrice;

    @Column
    private ZonedDateTime proposedStartAt;

    @Column
    private Integer duration;
}