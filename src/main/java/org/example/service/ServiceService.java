package org.example.service;

import org.example.entity.Service;
import org.example.entity.User;
import org.example.service.base.BaseService;

import java.util.List;

public interface ServiceService
        extends BaseService<Service, Long> {

    Service createService(Service service);
    List<Service> findByParentService(Service parentService);
}
