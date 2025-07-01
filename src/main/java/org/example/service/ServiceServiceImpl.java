package org.example.service;

import org.example.entity.Service;
import org.example.repository.ServiceRepository;
import org.example.service.base.BaseServiceImpl;


@org.springframework.stereotype.Service

public class ServiceServiceImpl
        extends BaseServiceImpl<Service, Long, ServiceRepository>
        implements ServiceService {

    public ServiceServiceImpl(ServiceRepository repository) {
        super(repository);
    }



}