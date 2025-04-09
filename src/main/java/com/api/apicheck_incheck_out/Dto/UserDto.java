package com.api.apicheck_incheck_out.Dto;

import com.example.demo.Entity.Notification;

import java.util.List;

public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private List<Notification> notifications;
}
