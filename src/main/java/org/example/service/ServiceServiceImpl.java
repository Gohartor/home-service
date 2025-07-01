package org.example.service;

import jakarta.transaction.Transactional;
import org.example.entity.Service;
import org.example.repository.ServiceRepository;
import org.example.service.base.BaseServiceImpl;

import java.util.List;


@org.springframework.stereotype.Service

public class ServiceServiceImpl
        extends BaseServiceImpl<Service, Long, ServiceRepository>
        implements ServiceService {

    public ServiceServiceImpl(ServiceRepository repository) {
        super(repository);
    }


    @Transactional
    @Override
    public Service createService(Service service) {
        Service parent = service.getParentService();
        long count = repository.countByNameParentService(service.getName(), parent);
        if (count > 0) {
            throw new IllegalArgumentException("service name already exist");
        }
        return save(service);
    }



    @Override
    public List<Service> findByParentService(Service parentService) {
        return repository.findByParentService(parentService);
    }


}