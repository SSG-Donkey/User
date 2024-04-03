package com.project.backend.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.backend.dto.UserDto;
import com.project.backend.mappers.UserMapper;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public List<UserDto> findAll() {
        System.out.println("-----------------여기 도착--------------");
        List<UserDto> res = userMapper.findAll();

        System.out.println(res);

        System.out.println("디버깅");
        for (UserDto temp : res) {
            System.out.println(temp.getUserId());
        }
        System.out.println("디버깅 끝");

        return res;
    }
}