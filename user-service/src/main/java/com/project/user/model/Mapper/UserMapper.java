package com.project.user.model.Mapper;

import com.project.user.model.Dto.UsersDto;
import com.project.user.model.Entity.Users;
import org.springframework.stereotype.Component;


@Component
public class UserMapper extends BaseMapper<Users, UsersDto>{

    @Override
    public Users ConvertToEntity(UsersDto dto, Object... args) {
        return Users.builder()
                .user_id(dto.getUser_id())
                .email(dto.getEmail())
                .address(dto.getAddress())
                .status(dto.getStatus())
                .build();
    }

    @Override
    public UsersDto ConvertToDto(Users entity, Object... args) {
        return UsersDto.builder()
                .user_id(entity.getUser_id())
                .email(entity.getEmail())
                .address(entity.getAddress())
                .status(entity.getStatus())
                .build();
    }
}
