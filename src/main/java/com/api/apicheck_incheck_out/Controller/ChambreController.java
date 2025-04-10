package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.ChambreDTO;
import com.api.apicheck_incheck_out.Entity.Chambre;
import com.api.apicheck_incheck_out.Mapper.ChambreMapper;
import com.api.apicheck_incheck_out.Repository.ChambreRepository;
import com.api.apicheck_incheck_out.Service.ChambreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chambres")
public class ChambreController {
    @Autowired
    private ChambreService chambreService;
    @Autowired
    private ChambreMapper chambreMapper;

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

    @GetMapping("/disponibles")
    public ResponseEntity<List<ChambreDTO>> getChambresDisponibles() {
        List<Chambre> chambresDisponibles = chambreService.getChambresDisponibles();

        List<ChambreDTO> chambreDTOList = chambresDisponibles.stream()
                .map(chambreMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }

    @PutMapping("/occupee/{id}")
    public ResponseEntity<Void> setChambreOccupee(@PathVariable Long id, @RequestParam Long idReservation) {
        chambreService.setChambreOccupee(id, idReservation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/disponible/{id}")
    public ResponseEntity<Void> setChambreDisponible(@PathVariable Long id) {
        chambreService.setChambreDisponible(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/reservation/{id}")
    public ResponseEntity<List<ChambreDTO>> getChambresByReservation(@PathVariable Long id) {
        List<Chambre> chambres = chambreService.getChambresByReservation(id);

        List<ChambreDTO> chambreDTOList = chambres.stream()
                .map(chambreMapper::toDTO)
                .collect(Collectors.toList());

        return new ResponseEntity<>(chambreDTOList, HttpStatus.OK);
    }
}
