package org.example.service;

import org.example.entity.ExpertService;
import org.example.entity.Order;
import org.example.repository.ExpertServiceRepository;
import org.example.repository.OrderRepository;
import org.example.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ExpertServiceServiceImpl
        extends BaseServiceImpl<ExpertService, Long, ExpertServiceRepository>
        implements ExpertServiceService {

    public ExpertServiceServiceImpl(ExpertServiceRepository repository) {
        super(repository);
    }



}