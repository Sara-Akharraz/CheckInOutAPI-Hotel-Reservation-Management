package com.api.apicheck_incheck_out.service.factory;

import com.api.apicheck_incheck_out.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class UserModifier {

    UserTypesFinder userTypesFinder;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
    public UserDto modify(Long id, UserDto user) {
        if(userTypesFinder.verifyExistance(id,user)) {
            UserDto userDto = userTypesFinder.findById(id);
            userDto.setCin(user.getCin());
            userDto.setEmail(user.getEmail());
            userDto.setNom(user.getNom());
            userDto.setPassword(encoder.encode(user.getPassword()));
            userDto.setPrenom(user.getPrenom());
            userDto.setRole(user.getRole());
            userDto.setTelephone(user.getTelephone());
            return userDto;
        }
        return null;
    }
}
