package com.api.apicheck_incheck_out.DTO;


import lombok.Data;

@Data
public class LogInRequest {
    private String email;
    private String password;
}