package org.example.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.base.BaseEntity;

@Entity
@Getter
@Setter
@ToString(callSuper=true)
@NoArgsConstructor
public class Expert extends BaseEntity {

    public static final String STATUS = "status";

    @Column(name = STATUS)
    private ExpertStatus status = ExpertStatus.PENDING;

}
