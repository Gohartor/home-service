package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;
import java.util.List;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
public class Service extends BaseEntity {
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double basePrice;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "parent_service_id")
    private Service parentService;

    @OneToMany(mappedBy = "parentService", cascade = CascadeType.ALL)
    private List<Service> subServices;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<ExpertService> expertServices;

    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    private List<Order> orders;
}