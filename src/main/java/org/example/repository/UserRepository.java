package org.example.repository;

import org.example.entity.User;
import org.example.repository.base.BaseRepository;

public interface UserRepository extends BaseRepository<User, Long> {

    //Optional<User> findByEmail(String email);
}