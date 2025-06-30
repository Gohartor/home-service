package org.example.entity.base;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.ZonedDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
public class BaseEntity implements Serializable {

    public static final String ID = "id";
    public static final String CREATE_DATE = "create_date";
    public static final String LAST_UPDATE_DATE = "last_update_date";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ID)
    private Long id;

    @Column(name = CREATE_DATE)
    private ZonedDateTime createDate;

    @Column(name = LAST_UPDATE_DATE)
    private ZonedDateTime lastUpdateDate;

    @PrePersist
    public void prePersist() {
        setCreateDate(ZonedDateTime.now());
        setLastUpdateDate(ZonedDateTime.now());
    }

    @PreUpdate
    public void preUpdate() {
        setLastUpdateDate(ZonedDateTime.now());
    }

}
