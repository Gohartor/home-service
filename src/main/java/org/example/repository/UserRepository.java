package org.example.repository;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.example.entity.User;
import org.example.entity.enumerator.ExpertStatus;
import org.example.entity.enumerator.RoleType;
import org.example.repository.base.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {

    //Optional<User> findByEmail(String email);

    // ۱. لیست متخصصین جدید یا در انتظار تایید
    List<User> findByRoleAndExpertStatus(RoleType role, ExpertStatus expertStatus);

    // ۲. جستجو متخصص بر اساس ID
    Optional<User> findByIdAndRole(Long id, RoleType role);

    boolean existsByEmail(String email);
}