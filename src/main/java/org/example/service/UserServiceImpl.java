package org.example.service;

import jakarta.transaction.Transactional;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.base.BaseServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl
        extends BaseServiceImpl<User, Long, UserRepository>
        implements UserService {

    public UserServiceImpl(UserRepository repository) {
        super(repository);
    }

    @Transactional
    public User registerUser(User user) {
        if (repository.existsById(user.getId())) {
            throw new RuntimeException("User with id " + user.getId() + " already exists");
        }
        return save(user);
    }


}