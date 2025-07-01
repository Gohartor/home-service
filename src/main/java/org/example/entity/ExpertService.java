package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;

@Entity
@Table(name = "expert_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpertService extends BaseEntity {

    @ManyToOne
    private User expert;

    @ManyToOne
    private Service service;
}