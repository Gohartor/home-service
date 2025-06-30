package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    private String fullName;

    private String email;

    private String password;

    private String profilePhoto;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    @Column(name = "expert_status")
    private ExpertStatus expertStatus;


    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private Wallet wallet;



    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL)
    private List<Proposal> proposals;

    @OneToMany(mappedBy = "specialist", cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Review> writtenReviews;
}