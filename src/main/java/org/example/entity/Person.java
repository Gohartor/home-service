package org.example.entity;


import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.base.BaseEntity;

@Getter
@Setter
@ToString
@MappedSuperclass
public class Person extends BaseEntity {

    private static final String FIRST_NAME = "first_name";
    private static final String LAST_NAME = "last_name";
    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @Column(name = FIRST_NAME)
    private String firstName;

    @Column(name = LAST_NAME)
    private String lastName;

    @Column(name = EMAIL)
    private String email;

    @Column(name = PASSWORD)
    private String password;


}
