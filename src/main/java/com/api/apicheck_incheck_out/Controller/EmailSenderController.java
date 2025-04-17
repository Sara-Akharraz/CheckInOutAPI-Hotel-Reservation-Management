package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.Service.Impl.EmailSenderService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailSenderController {

    @Autowired
    private EmailSenderService emailSenderService;

    @GetMapping("/email")
    public ResponseEntity<String> sendEmail(
            @RequestParam String to,
            @RequestParam(defaultValue = "test de mail") String subject,
            @RequestParam(defaultValue="Ceci est un mail de test") String body
    ){
        try{
            emailSenderService.sendEmail(to,subject,body);
            return ResponseEntity.ok("Email envoyé à "+ to);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur :"+e.getMessage());
        }
    }
}
