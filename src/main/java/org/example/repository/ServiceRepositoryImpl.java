package org.example.repository;

import org.example.entity.Service;
import org.example.repository.base.BaseRepository;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;


@Repository
public class ServiceRepositoryImpl
        extends BaseRepositoryImpl<Service, Long>
        implements ServiceRepository  {


}
