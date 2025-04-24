package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.CheckOutDTO;
import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Enums.CheckOutStatut;
import com.api.apicheck_incheck_out.Mapper.CheckOutMapper;
import com.api.apicheck_incheck_out.Service.CheckOutService;
import com.api.apicheck_incheck_out.Service.StripePayment.StripeResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/checkout")
public class CheckOutController {
    @Autowired
    CheckOutService checkOutService;

    @Autowired
    CheckOutMapper checkOutMapper;

    @GetMapping
    public ResponseEntity<?> getCheckOuts() {
        try {
            List<Check_Out> checkouts = checkOutService.getAllCheckOuts();
            return new ResponseEntity<>(checkOutMapper.toDTOList(checkouts), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id_checkOut}")
    public ResponseEntity<?> getCheckOut(@PathVariable("id_checkOut") Long id_checkOut) {
        try{
            return ResponseEntity.ok(checkOutMapper.toDTO(checkOutService.getCheckOutById(id_checkOut)));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }
    @PostMapping
    public ResponseEntity<?> addCheckOut(@RequestBody CheckOutDTO check_out) {
        try{
            Check_Out checkout = checkOutMapper.toEntity(check_out);
            return ResponseEntity.ok(checkOutMapper.toDTO(checkOutService.addCheckOut(checkout)));
        }catch(Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/{status}")
    public ResponseEntity<?> setCheck_OutStatus(@PathVariable("id") Long id, @PathVariable("status") String status) {
        try{
            return ResponseEntity.ok(checkOutMapper.toDTO(checkOutService.setCheck_OutStatus(id, CheckOutStatut.valueOf(status))));
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{isValid}/{id}")
    public boolean isValidCheckOut(@PathVariable("id") Long id){
        return checkOutService.isValidCheckOut(id);
    }
    @GetMapping("/amount/{id}")
    public Long getAmount(@PathVariable("id") Long id){
        return checkOutService.getAmount(id);
    }

    @PostMapping("/payer/{id_checkout}")
    public StripeResponse payer(@PathVariable("id_checkout") Long id){
        try{
            return checkOutService.payer(id);
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,e.getMessage());
        }
    }
}
