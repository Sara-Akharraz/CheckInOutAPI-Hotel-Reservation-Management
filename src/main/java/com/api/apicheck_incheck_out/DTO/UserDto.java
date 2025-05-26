package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Entity.Notification;
import com.api.apicheck_incheck_out.Enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    @JsonIgnore
    private List<Notification> notifications;
    private String cin;
    private String numeroPassport;
    private Role role;
    private String telephone;

}
