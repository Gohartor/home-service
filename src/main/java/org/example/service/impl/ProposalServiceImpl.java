package org.example.service.impl;

import org.example.entity.Order;
import org.example.entity.Proposal;
import org.example.entity.User;
import org.example.entity.enumerator.ServiceStatus;
import org.example.repository.OrderRepository;
import org.example.repository.ProposalRepository;
import org.example.service.OrderService;
import org.example.service.ProposalService;
import org.example.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProposalServiceImpl implements ProposalService {

    private final ProposalRepository repository;
    private final OrderService orderService;
    private final UserService userService;

    public ProposalServiceImpl(ProposalRepository repository, OrderService orderService, UserService userService) {
        this.repository = repository;
        this.orderService = orderService;
        this.userService = userService;
    }

    @Override
    public Proposal save(Proposal entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Proposal> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Proposal> findAll() {
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

    public long countAllByOrder_Id(Long orderId){
        return repository.countAllByOrder_Id(orderId);
    }


}