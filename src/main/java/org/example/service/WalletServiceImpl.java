package org.example.service;

import org.example.entity.Order;
import org.example.entity.Wallet;
import org.example.repository.OrderRepository;
import org.example.repository.WalletRepository;
import org.example.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class WalletServiceImpl
        extends BaseServiceImpl<Wallet, Long, WalletRepository>
        implements WalletService {

    public WalletServiceImpl(WalletRepository repository) {
        super(repository);
    }



}