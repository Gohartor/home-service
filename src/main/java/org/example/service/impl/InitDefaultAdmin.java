package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.entity.User;
import org.example.entity.enumerator.RoleType;
import org.example.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

@Component
@RequiredArgsConstructor
public class InitDefaultAdmin implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String email = "admin";
        if(userRepository.existsByEmail(email)){
            return;
        }
        User admin = new User();
        admin.setEmail(email);
        admin.setRole(RoleType.ADMIN);
        admin.setPassword(passwordEncoder.encode("admin"));
        userRepository.save(admin);
        System.out.println(admin.getId());
    }

}
