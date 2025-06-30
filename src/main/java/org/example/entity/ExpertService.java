package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;

@Entity
@Table(name = "expert_services")
@Getter
@Setter
@NoArgsConstructor
public class ExpertService extends BaseEntity {


    @ManyToOne
    @JoinColumn(name = "specialist_id", nullable = false)
    private User specialist;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
}