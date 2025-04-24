package com.api.apicheck_incheck_out.Controller;


import com.api.apicheck_incheck_out.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    @GetMapping("/")
    public String welcome(HttpServletRequest request) {
        return "Welcome Hasnae ! " +
                "+" +
                "" + request.getSession().getId();
    }
//    private final UserService userService;
//
//    @PostMapping("/login")
//    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request){
//        return ResponseEntity.ok(userService.login(request));
//    }
}
