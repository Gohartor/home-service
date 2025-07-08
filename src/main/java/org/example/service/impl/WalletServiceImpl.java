package org.example.service.impl;

import org.example.entity.Wallet;
import org.example.repository.WalletRepository;
import org.example.service.WalletService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WalletServiceImpl implements WalletService {

    private final WalletRepository repository;

    public WalletServiceImpl(WalletRepository repository) {
        this.repository = repository;
    }

    @Override
    public Wallet save(Wallet entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Wallet> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Wallet> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return repository.existsById(id);
    }
}