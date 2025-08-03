package org.example.repository;

import org.example.entity.Service;
import org.example.repository.base.BaseRepository;

import java.util.List;

public interface ServiceRepository
        extends BaseRepository<Service, Long> {

    long countByNameAndParentService(String name, Service parentService);

//    List<Service> findByParentServiceIsNull();

    boolean existsByNameAndParentService(String name, Service parentService);

    boolean existsByParentService_Id(Long parentServiceId);

//    Optional<Service> findByNameAndParentService(String name, Service parentService);

    List<Service> findByParentService(Service parentService);

}