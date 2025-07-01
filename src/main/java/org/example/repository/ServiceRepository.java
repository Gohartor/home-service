package org.example.repository;

import org.example.entity.Service;
import org.example.entity.User;
import org.example.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ServiceRepository
        extends BaseRepository<Service, Long> {

    long countByNameParentService(String name, Service parentService);
    List<Service> findByParentService(Service parentService);

}