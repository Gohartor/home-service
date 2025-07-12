package org.example.service;

import org.example.dto.expert.ExpertRegisterDto;
import org.example.dto.expert.ExpertResponseDto;
import org.example.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

//    User save(User entity);
//    List<User> findAll();
//    void deleteById(Long id);
//    boolean existsById(Long id);
//    boolean isManager(User user);

    Optional<User> findById(Long id);

    List<ExpertResponseDto> listPendingExperts();
    void approveExpert(Long expertId);
    void rejectExpert(Long expertId);
    void addExpertToService(Long expertId, Long serviceId);
    void removeExpertFromService(Long expertId, Long serviceId);

    void registerExpert(ExpertRegisterDto dto);


}