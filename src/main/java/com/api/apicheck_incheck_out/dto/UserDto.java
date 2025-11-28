package com.api.apicheck_incheck_out.dto;

import com.api.apicheck_incheck_out.entity.Notification;
import com.api.apicheck_incheck_out.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@Builder
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
