package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.dto.ChambreDTO;
import com.api.apicheck_incheck_out.entity.Chambre;
import com.api.apicheck_incheck_out.enums.ChambreType;
import com.api.apicheck_incheck_out.mapper.ChambreMapper;
import com.api.apicheck_incheck_out.service.ChambreService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/chambre")
public class ChambreController {

    private final ChambreService chambreService;

    private final ChambreMapper chambreMapper;

    public ChambreController(ChambreService chambreService, ChambreMapper chambreMapper) {
        this.chambreService = chambreService;
        this.chambreMapper = chambreMapper;
    }

    @PostMapping
    public ResponseEntity<ChambreDTO> addChambre(@RequestParam ChambreDTO chambreDTO){
        Chambre chambre=chambreService.addChambre(chambreMapper.toEntity(chambreDTO));
        return ResponseEntity.ok(chambreMapper.toDTO(chambre));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ChambreDTO> updateChambre(@PathVariable Long id,@RequestBody ChambreDTO chambreDTO){
        Chambre chambre=chambreService.updateChambre(id,chambreMapper.toEntity(chambreDTO));
        return ResponseEntity.ok(chambreMapper.toDTO(chambre));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChambre(@PathVariable Long id){
        chambreService.deleteChambre(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<ChambreDTO>> getAllChambres() {
        List<Chambre> chambres = chambreService.getChambres();
        List<ChambreDTO> chambreDTOList = chambres.stream()
                .map(chambreMapper::toDTO)
                .toList();

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChambreDTO> getChambreById(@PathVariable Long id) {
        Chambre chambre = chambreService.getChambre(id);

        ChambreDTO chambreDTO = chambreMapper.toDTO(chambre);

        return new ResponseEntity<>(chambreDTO, HttpStatus.OK);
    }
    @GetMapping("/types")
    public ResponseEntity<List<String>> getAllChambreTypes() {
        List<String> types = Arrays.stream(ChambreType.values())
                .map(Enum::name)
                .toList();
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

}
