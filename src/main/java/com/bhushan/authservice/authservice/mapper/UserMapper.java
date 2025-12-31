package com.bhushan.authservice.authservice.mapper;

import com.bhushan.authservice.authservice.dto.CreateUserDto;
import com.bhushan.authservice.authservice.dto.UserResponseDto;
import com.bhushan.authservice.authservice.entity.User;
import com.bhushan.authservice.authservice.entity.UserProfile;
import com.bhushan.authservice.authservice.service.UserService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "firstName", source = "userProfile.firstName")
    @Mapping(target = "lastName", source="userProfile.lastName")
    @Mapping(target = "mobileNumber",source = "userProfile.mobileNumber")
    UserResponseDto toDto(User user);

    @Mapping(target = "userProfile",ignore = true)
    User toUserEntity(CreateUserDto createUserDto);

    @Mapping(target = "userId",ignore = true)
    @Mapping(target = "user",ignore = true)
    UserProfile toUserProfileEntity(CreateUserDto createUserDto);


    List<UserResponseDto> toUserDtoList(List<User> users);

}
