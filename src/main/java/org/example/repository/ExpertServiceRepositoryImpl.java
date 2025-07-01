package org.example.repository;

import org.example.entity.ExpertService;
import org.example.entity.Order;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class ExpertServiceRepositoryImpl
        extends BaseRepositoryImpl<ExpertService, Long>
        implements ExpertServiceRepository  {


}
