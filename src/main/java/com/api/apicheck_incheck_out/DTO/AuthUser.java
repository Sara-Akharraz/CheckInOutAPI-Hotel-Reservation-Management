package com.api.apicheck_incheck_out.DTO;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthUser {

    String email;
    String password;
}
