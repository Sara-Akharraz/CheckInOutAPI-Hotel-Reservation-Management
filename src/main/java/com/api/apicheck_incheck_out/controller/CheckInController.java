package com.api.apicheck_incheck_out.controller;

import com.api.apicheck_incheck_out.dto.ApiResponse;
import com.api.apicheck_incheck_out.dto.CheckInDTO;
import com.api.apicheck_incheck_out.dto.DocumentScanDTO;
import com.api.apicheck_incheck_out.entity.DocumentScan;
import com.api.apicheck_incheck_out.mapper.DocumentScanMapper;
import com.api.apicheck_incheck_out.repository.ReservationRepository;
import com.api.apicheck_incheck_out.entity.CheckIn;
import com.api.apicheck_incheck_out.entity.Reservation;
import com.api.apicheck_incheck_out.enums.CheckInStatus;
import com.api.apicheck_incheck_out.enums.DocumentScanType;
import com.api.apicheck_incheck_out.mapper.CheckInMapper;
import com.api.apicheck_incheck_out.service.CheckInService;
import com.api.apicheck_incheck_out.service.ReservationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/check_in")
public class CheckInController {
    private final CheckInService checkInService;
    private final ReservationService reservationService;
    private final DocumentScanMapper documentScanMapper;
    private final ReservationRepository reservationRepository;
    private final CheckInMapper checkInMapper;

    private static final String SUCCESS = "success";
    private static final String ERROR = "error";


    public CheckInController(CheckInService checkInService, ReservationService reservationService, DocumentScanMapper documentScanMapper, ReservationRepository reservationRepository, CheckInMapper checkInMapper) {
        this.checkInService = checkInService;
        this.reservationService = reservationService;
        this.documentScanMapper = documentScanMapper;
        this.reservationRepository = reservationRepository;
        this.checkInMapper = checkInMapper;
    }

    @GetMapping("/today-checkins")
    public ResponseEntity<List<CheckInDTO>> checkInsForToday(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<CheckIn> checkouts = checkInService.checkinsForToday(date);
        List<CheckInDTO> dtos = checkouts.stream()
                .map(checkInMapper::toDTO)
                .toList();
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/validerScan")
    public ResponseEntity<Map<String, Object>> validerScan(@RequestParam Long reservationId,
                                                           @RequestParam("file") MultipartFile file,
                                                           @RequestParam String nom,
                                                           @RequestParam String prenom,
                                                           @RequestParam String cin,
                                                           @RequestParam String type) {
        Map<String, Object> response = new HashMap<>();

        try {

            Reservation reservation = reservationService.getReservationById(reservationId);

            // Vérification du type MIME
            if (!Objects.equals(file.getContentType(), "image/jpeg") &&
                    !Objects.equals(file.getContentType(), "image/png")) {
                response.put(SUCCESS, false);
                response.put(ERROR, "Format de fichier non supporté.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            //  la taille
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put(SUCCESS, false);
                response.put(ERROR, "Le fichier est trop volumineux.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Convertir en tableau d’octets
            byte[] imageBytes = file.getBytes();

            // Vérification des champs
            if (nom == null || nom.isEmpty() || prenom == null || prenom.isEmpty() || cin == null || cin.isEmpty()) {
                response.put(SUCCESS, false);
                response.put(ERROR, "Les informations (nom, prénom, CIN) sont requises.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Type de document
            if (!EnumUtils.isValidEnum(DocumentScanType.class, type.toUpperCase())) {
                response.put(SUCCESS, false);
                response.put(ERROR, "Type de document invalide.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            DocumentScanType documentType = DocumentScanType.valueOf(type.toUpperCase());

            DocumentScanDTO doc = new DocumentScanDTO();
            doc.setNom(nom);
            doc.setPrenom(prenom);
            doc.setCin(cin);
            doc.setType(documentType);
            doc.setImage(imageBytes);
            doc.setFileName(file.getOriginalFilename());
            doc.setFileType(file.getContentType());


            boolean result = checkInService.validerScan(reservation, doc);

            if (result) {
                response.put(SUCCESS, true);
                response.put("message", "Scan validé avec succès ");
                return ResponseEntity.ok(response);
            } else {
                response.put(SUCCESS, false);
                response.put(ERROR, "Échec de validation du document check-in.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (IOException e) {
            response.put(SUCCESS, false);
            response.put(ERROR, "Erreur lors de la lecture du fichier.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (RuntimeException e) {
            response.put(SUCCESS, false);
            response.put(ERROR, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }


    @GetMapping("/{id}/document")
    public ResponseEntity<DocumentScanDTO> getDocumentScanByCheckIn(@PathVariable Long id) {
        try {
            DocumentScanDTO doc = documentScanMapper.toDTO(checkInService.getDocumentByCheckin(id));
            return ResponseEntity.ok(doc);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/validerCheckIn")
    public ResponseEntity<String> validerCheckIn(@RequestParam Long reservationId) {
        try {

            log.debug("Début de la validation du check-in pour la réservation ID: " + reservationId);


            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                log.debug("Réservation non trouvée pour l'ID: " + reservationId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Réservation non trouvée.");
            }


            log.debug("Réservation trouvée : " + reservation);


            boolean result = checkInService.validerCheckIn(reservation);


            if (result) {
                log.info("Check-in validé avec succès pour la réservation ID: " + reservationId);
                return ResponseEntity.ok("Check-in validé avec succès.");
            } else {
                log.error("Échec de la validation du check-in pour la réservation ID: " + reservationId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Échec de validation du check-in.");
            }
        } catch (Exception e) {

            log.error("Erreur lors de la validation du check-in pour la réservation ID: " + reservationId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur interne s'est produite.");
        }
    }

    @GetMapping("/reservation/{idReservation}")
    public ResponseEntity<ApiResponse<CheckInDTO>> getCheckInByReservation(@PathVariable Long idReservation) {
        try {
            log.info("===> Recherche Check-in pour réservation ID: " + idReservation);
            CheckIn checkIn = checkInService.getCheckInByReservation(idReservation);
            if (checkIn != null) {
                return ResponseEntity.ok(new ApiResponse<>(true, "Check-in trouvé.", checkInMapper.toDTO(checkIn)));
            } else {
                log.debug("===> checkIn is null, checking reservation existence...");
                boolean exists = reservationService.existsById(idReservation);
                log.debug("===> Reservation exists? " + exists);
                if (reservationService.existsById(idReservation)) {
                    return ResponseEntity.ok(new ApiResponse<>(true,"Check-in non encore effectué.",null));
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Réservation introuvable.", null));
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }


    @GetMapping("/status/{idReservation}")
    public ResponseEntity<CheckInStatus> getStatusCheckIn(@PathVariable Long idReservation) {
        CheckInStatus status = checkInService.getStatusCheckIn(idReservation);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/validercheckinreception")
    public ResponseEntity<String> validercheckinReception(@RequestParam Long idCheckin) {
        checkInService.validerCheckinReception(idCheckin);
        return ResponseEntity.ok("Check-in validé avec succès");
    }

    @GetMapping("/status")
    public ResponseEntity<String> getCheckInStatus(@RequestParam Long reservationId) {
        Optional<Reservation> reservationOptional = reservationRepository.findById(reservationId);

        if (reservationOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("not_found");
        }

        Reservation reservation = reservationOptional.get();

        if (reservation.getCheckIn() == null) {
            return ResponseEntity.ok("no_checkin");
        }

        CheckInStatus statutCheckIn = reservation.getCheckIn().getStatus();

        return ResponseEntity.ok(statutCheckIn.toString());
    }
    @PostMapping(value = "ajoutercheckin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> ajoutercheckin(
            @RequestParam Long idReservation,
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam DocumentScanType type,
            @RequestParam(required = false) String cin,
            @RequestParam(required = false) String passport,
            @RequestPart MultipartFile image
    ) throws IOException {

        DocumentScanDTO dto = new DocumentScanDTO();
        dto.setNom(nom);
        dto.setPrenom(prenom);
        dto.setType(type);
        dto.setCin(cin);
        dto.setPassport(passport);
        dto.setImage(image.getBytes());
        dto.setFileName(image.getOriginalFilename());
        dto.setFileType(image.getContentType());

        DocumentScan documentScan = documentScanMapper.toEntity(dto, null);
        checkInService.ajoutercheckinReception(idReservation, documentScan);

        return ResponseEntity.ok("Check-in ajouté avec succès");
    }
}


