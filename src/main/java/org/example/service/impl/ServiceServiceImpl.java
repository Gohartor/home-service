package org.example.service.impl;

import org.example.entity.Service;
import org.example.entity.User;
import org.example.repository.ServiceRepository;
import org.example.service.ServiceService;
import org.example.service.UserService;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository repository;
    private final UserService userService;

    public ServiceServiceImpl(ServiceRepository repository, UserService userService) {
        this.repository = repository;
        this.userService = userService;
    }

    @Override
    public Service save(Service entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<Service> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Service> findAll() {
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

    @Override
    @Transactional
    public Service createService(Service service) {
        User currentUser = userService.findById(1L).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (userService.isManager(currentUser)) {
            throw new SecurityException("Only managers can create services!");
        }
        Service parent = service.getParentService();
        long count = repository.countByNameAndParentService(service.getName(), parent);
        if (count > 0) {
            throw new IllegalArgumentException("Service name already exists under this parent!");
        }
        return save(service);
    }

    @Override
    public List<Service> findByParentService(Service parentService) {
        User currentUser = userService.findById(1L).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (userService.isManager(currentUser)) {
            throw new SecurityException("Only managers can view services!");
        }
        return repository.findByParentService(parentService);
    }
}