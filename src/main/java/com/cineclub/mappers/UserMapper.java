package com.cineclub.mappers;

import com.cineclub.dtos.UserDto;
import com.cineclub.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);
}
