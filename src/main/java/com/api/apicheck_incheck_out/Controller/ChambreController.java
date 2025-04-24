package com.api.apicheck_incheck_out.Controller;
import com.api.apicheck_incheck_out.DTO.ChambreDTO;
import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Enums.ChambreType;
import com.api.apicheck_incheck_out.Mapper.ChambreMapper;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Service.ChambreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chambres")
public class ChambreController {
    @Autowired
    private ChambreService chambreService;
    @Autowired
    private ChambreMapper chambreMapper;

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
                .map(chambre -> chambreMapper.toDTO(chambre))
                .collect(Collectors.toList());

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
                .collect(Collectors.toList());
        return new ResponseEntity<>(types, HttpStatus.OK);
    }

}