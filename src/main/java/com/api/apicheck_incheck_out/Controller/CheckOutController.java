package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Service.CheckOutService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/checkout")
public class CheckOutController {

    CheckOutService checkOutService;
    @GetMapping
    public ResponseEntity<?> getCheckOuts() {
        try {
            List<Check_Out> epics = checkOutService.getAllCheckOuts();
            return new ResponseEntity<>(epics, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id_checkOut}")
    public ResponseEntity<?> getCheckOut(@PathVariable("id_checkOut") Long id_checkOut) {
        try{
            Check_Out check_out = checkOutService.getCheckOutById(id_checkOut);
            return new ResponseEntity<>(check_out, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
}
