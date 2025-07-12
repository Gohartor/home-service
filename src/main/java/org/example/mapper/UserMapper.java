package org.example.mapper;

import org.example.dto.expert.ExpertResponseDto;
import org.example.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ExpertResponseDto toExpertResponseDto(User user);

}
