package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String profilePhoto;
//    private Byte[] profilePhoto;

    private RoleType role;

    private ExpertStatus expertStatus;
}