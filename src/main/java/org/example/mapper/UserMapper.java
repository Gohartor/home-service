package org.example.mapper;

import org.example.dto.expert.ExpertRegisterDto;
import org.example.dto.expert.ExpertResponseDto;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ExpertResponseDto toExpertResponseDto(User user);

    @Mapping(target = "role", expression = "java(org.example.entity.enumerator.RoleType.EXPERT)")
    @Mapping(target = "expertStatus", expression = "java(org.example.entity.enumerator.ExpertStatus.NEW)")
    @Mapping(target = "profilePhoto", ignore = true)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createDate", ignore = true)
//    @Mapping(target = "lastUpdateDate", ignore = true)
    User fromExpertRegisterDto(ExpertRegisterDto dto);

}
