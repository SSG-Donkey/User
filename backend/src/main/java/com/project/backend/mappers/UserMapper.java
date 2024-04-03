package com.project.backend.mappers;


import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.project.backend.dto.UserDto;

import java.util.List;

@Mapper
@Repository
public interface UserMapper {

     List<UserDto> findAll();
}