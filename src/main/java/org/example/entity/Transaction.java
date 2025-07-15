package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.entity.base.BaseEntity;
import org.example.entity.enumerator.TransactionType;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction extends BaseEntity {


    private Double amount;
    private TransactionType type;
    private Long relatedOrderId;

    @ManyToOne
    private Wallet wallet;



}