package org.example.repository;

import org.example.entity.Service;
import org.example.entity.User;
import org.example.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface ServiceRepository
        extends BaseRepository<Service, Long> {

    long countByNameAndParentService(String name, Service parentService);

    List<Service> findByParentServiceIsNull();

    boolean existsByNameAndParentService(String name, Service parentService);

    Optional<Service> findByNameAndParentService(String name, Service parentService);

    List<Service> findByParentService(Service parentService);

}