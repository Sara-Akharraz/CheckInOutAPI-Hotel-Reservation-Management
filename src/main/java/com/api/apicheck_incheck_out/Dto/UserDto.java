package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Entity.Notification;
import com.api.apicheck_incheck_out.Enum.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private Role role;
    private List<Notification> notifications=new ArrayList<>();
    private String cin;
    private String numeroPassport;

}
