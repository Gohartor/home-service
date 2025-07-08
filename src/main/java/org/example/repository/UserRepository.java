package org.example.repository;

import org.example.entity.User;
import org.example.repository.base.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long> {

    //List<User> findByEmail(String email);
}