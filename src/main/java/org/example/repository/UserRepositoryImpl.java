package org.example.repository;

import org.example.entity.User;
import org.example.repository.base.BaseRepositoryImpl;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl
        extends BaseRepositoryImpl<User, Long>
        implements UserRepository {


}
