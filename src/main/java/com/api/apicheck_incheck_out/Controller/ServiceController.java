package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.Entity.Services;
import com.api.apicheck_incheck_out.Service.Impl.ServicesServiceImpl;
import com.api.apicheck_incheck_out.Service.ServicesService;
import org.apache.catalina.connector.Response;
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

    @PutMapping("/{id_service}")
    public ResponseEntity<Services> updateService(@PathVariable Long id_service,@RequestBody Services service){
        return ResponseEntity.ok(servicesService.updateService(id_service,service));
    }
    @DeleteMapping("/{id_service}")
    public ResponseEntity<Services> deleteService(@PathVariable Long id_service){
        return ResponseEntity.ok(servicesService.deleteService(id_service));
    }

}
