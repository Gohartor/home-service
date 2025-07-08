package org.example.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;
import org.example.entity.enumerator.ServiceStatus;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Service extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    private Double basePrice;

    private String description;

    @ManyToOne
    private Service parentService;


    @ManyToMany(mappedBy = "services")
    private Set<User> experts = new HashSet<>();

    @OneToMany(mappedBy = "service")
    private Set<Proposal> proposals = new HashSet<>();


}
