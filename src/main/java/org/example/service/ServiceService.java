package org.example.service;

import org.example.entity.Service;

import java.util.List;
import java.util.Optional;

public interface ServiceService {
    Service save(Service entity);
    Optional<Service> findById(Long id);
    List<Service> findAll();
    void deleteById(Long id);
    boolean existsById(Long id);
    Service createService(Service service);
    List<Service> findByParentService(Service parentService);
}