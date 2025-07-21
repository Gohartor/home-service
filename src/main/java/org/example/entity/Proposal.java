package org.example.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class Proposal extends BaseEntity {

    private Double proposedPrice;

    private ZonedDateTime proposedStartAt;

    private Integer duration;

    @Column(nullable = false)
    private boolean isAccepted = false;

    @ManyToOne
    private Order order;

    @ManyToOne
    private User expert;

}



