package com.ekufrin.vhsrental.user;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO toDTO(User user);

    default Page<UserDTO> toDTO(Page<User> users) {
        return users.map(this::toDTO);
    }
}