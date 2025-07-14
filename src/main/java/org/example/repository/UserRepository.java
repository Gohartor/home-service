package org.example.repository;

import org.example.dto.admin.UserSearchFilterDto;
import org.example.entity.User;
import org.example.entity.enumerator.ExpertStatus;
import org.example.entity.enumerator.RoleType;
import org.example.repository.base.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {

    Optional<User> findByEmail(String email);


    List<User> findByRoleAndExpertStatus(RoleType role, ExpertStatus expertStatus);

    Optional<User> findByIdAndRole(Long id, RoleType role);

    boolean existsByEmail(String email);

    Page<User> findAll(Specification<User> spec, Pageable pageable);

//    Page<User> searchUsers(UserSearchFilterDto filter, Pageable pageable);

}