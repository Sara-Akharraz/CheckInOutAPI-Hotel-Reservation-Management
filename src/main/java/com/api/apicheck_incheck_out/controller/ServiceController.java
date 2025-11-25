package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.entity.Services;
import com.api.apicheck_incheck_out.service.ServicesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
public class ServiceController {
    private final ServicesService servicesService;

    public ServiceController(ServicesService service) {
        this.servicesService = service;
    }

    @PostMapping()
    public ResponseEntity<Services> addService(@RequestBody Services service){
        return ResponseEntity.ok(servicesService.addService(service));
    }
    @GetMapping()
    public ResponseEntity<List<Services>> getAllServices(){
        return ResponseEntity.ok(servicesService.getAllServices());
    }

    @PutMapping("/{idService}")
    public ResponseEntity<Services> updateService(@PathVariable Long idService,@RequestBody Services service){
        return ResponseEntity.ok(servicesService.updateService(idService,service));
    }
    @DeleteMapping("/{idService}")
    public ResponseEntity<Services> deleteService(@PathVariable Long idService){
        return ResponseEntity.ok(servicesService.deleteService(idService));
    }

}
