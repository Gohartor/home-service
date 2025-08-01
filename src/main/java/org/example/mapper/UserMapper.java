package org.example.mapper;

import org.example.dto.admin.UserAdminListDto;
import org.example.dto.admin.UserSearchFilterDto;
import org.example.dto.customer.CustomerRegisterDto;
import org.example.dto.customer.CustomerUpdateProfileDto;
import org.example.dto.expert.*;
import org.example.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ExpertResponseDto toExpertResponseDto(User user);

    @Mapping(target = "role", expression = "java(org.example.entity.enumerator.RoleType.EXPERT)")
    @Mapping(target = "expertStatus", expression = "java(org.example.entity.enumerator.ExpertStatus.NEW)")
    @Mapping(target = "profilePhoto", ignore = true)
    User fromExpertRegisterDto(ExpertRegisterDto dto);


    @Mapping(target = "role", constant = "CUSTOMER")
    @Mapping(target = "negativeScore", constant = "0L")
    @Mapping(target = "expertStatus", ignore = true)
    @Mapping(target = "profilePhoto", ignore = true)
    @Mapping(target = "services", ignore = true)
    User fromCustomerRegisterDto(CustomerRegisterDto dto);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profilePhoto", ignore = true)
    void updateExpertProfileFromDto(ExpertUpdateProfileDto dto, @MappingTarget User user);


    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().name() : null)")
    @Mapping(target = "services", expression = "java(user.getServices() != null ? user.getServices().stream().map(org.example.entity.Service::getName).toList() : null)")
    UserAdminListDto toUserAdminListDto(User user);


    ExpertProfileDto mapToProfileDto(User expert);


    ExpertLoginResponseDto fromEntityToExpertLoginResponseDto(User expert);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "profilePhoto", ignore = true)
    void updateCustomerProfileFromDto(CustomerUpdateProfileDto dto,@MappingTarget User user);
}
