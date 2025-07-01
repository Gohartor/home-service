package org.example.repository;

import org.example.entity.Service;
import org.example.entity.User;
import org.example.repository.base.BaseRepository;
import org.springframework.stereotype.Repository;

public interface ServiceRepository
        extends BaseRepository<Service, Long> {

}