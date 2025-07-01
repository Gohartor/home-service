package org.example.repository;

import org.example.entity.Order;
import org.example.entity.Wallet;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class WalletRepositoryImpl
        extends BaseRepositoryImpl<Wallet, Long>
        implements WalletRepository  {


}
