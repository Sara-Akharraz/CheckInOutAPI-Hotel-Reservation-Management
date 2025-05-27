package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.DTO.CheckInDTO;
import com.api.apicheck_incheck_out.DTO.DocumentScanDTO;
import com.api.apicheck_incheck_out.Entity.DocumentScan;
import com.api.apicheck_incheck_out.Mapper.DocumentScanMapper;
import com.api.apicheck_incheck_out.Repository.ReservationRepository;
import com.api.apicheck_incheck_out.Service.DocumentScanService;
import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enums.CheckInStatus;
import com.api.apicheck_incheck_out.Enums.DocumentScanType;
import com.api.apicheck_incheck_out.Mapper.CheckInMapper;
import com.api.apicheck_incheck_out.Service.CheckInService;
import com.api.apicheck_incheck_out.Service.ReservationService;
import com.api.apicheck_incheck_out.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/check_in")
public class CheckInController {
    private final CheckInService checkInService;
    private final DocumentScanService documentScanService;
    private final ReservationService reservationService;
    private final DocumentScanMapper documentScanMapper;
    private final CheckInMapper checkInMapper;
    private final ReservationRepository reservationRepository;


    public CheckInController(CheckInService checkInService, DocumentScanService documentScanService, ReservationService reservationService, DocumentScanMapper documentScanMapper, CheckInMapper checkInMapper, ReservationRepository reservationRepository) {
        this.checkInService = checkInService;
        this.documentScanService = documentScanService;
        this.reservationService = reservationService;
        this.documentScanMapper = documentScanMapper;
        this.checkInMapper = checkInMapper;
        this.reservationRepository = reservationRepository;
    }

    @GetMapping("/today-checkins")
    public ResponseEntity<List<CheckInDTO>> checkInsForToday(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        List<Check_In> checkouts = checkInService.checkinsForToday(date);
        List<CheckInDTO> dtos = checkouts.stream()
                .map(checkout -> checkInMapper.toDTO(checkout))
                .collect(Collectors.toList());
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
            if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/png")) {
                response.put("success", false);
                response.put("error", "Format de fichier non supporté.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            //  la taille
            if (file.getSize() > 5 * 1024 * 1024) {
                response.put("success", false);
                response.put("error", "Le fichier est trop volumineux.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Convertir en tableau d’octets
            byte[] imageBytes = file.getBytes();

            // Vérification des champs
            if (nom == null || nom.isEmpty() || prenom == null || prenom.isEmpty() || cin == null || cin.isEmpty()) {
                response.put("success", false);
                response.put("error", "Les informations (nom, prénom, CIN) sont requises.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // Type de document
            DocumentScanType documentType;
            try {
                documentType = DocumentScanType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                response.put("success", false);
                response.put("error", "Type de document invalide.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }


            DocumentScanDTO doc = new DocumentScanDTO();
            doc.setNom(nom);
            doc.setPrenom(prenom);
            doc.setCin(cin);
            doc.setType(documentType);
            doc.setImage(imageBytes);
            doc.setFileName(file.getOriginalFilename());
            doc.setFileType(file.getContentType());


            Boolean result = checkInService.validerScan(reservation, doc);

            if (result) {
                response.put("success", true);
                response.put("message", "Scan validé avec succès ");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Échec de validation du document check-in.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

        } catch (IOException e) {
            response.put("success", false);
            response.put("error", "Erreur lors de la lecture du fichier.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (RuntimeException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
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

            System.out.println("Début de la validation du check-in pour la réservation ID: " + reservationId);


            Reservation reservation = reservationService.getReservationById(reservationId);
            if (reservation == null) {
                System.out.println("Réservation non trouvée pour l'ID: " + reservationId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Réservation non trouvée.");
            }


            System.out.println("Réservation trouvée : " + reservation);


            Boolean result = checkInService.validerCheckIn(reservation);


            if (result) {
                System.out.println("Check-in validé avec succès pour la réservation ID: " + reservationId);
                return ResponseEntity.ok("Check-in validé avec succès.");
            } else {
                System.out.println("Échec de la validation du check-in pour la réservation ID: " + reservationId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Échec de validation du check-in.");
            }
        } catch (Exception e) {

            System.out.println("Erreur lors de la validation du check-in pour la réservation ID: " + reservationId);
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Une erreur interne s'est produite.");
        }
    }

    @GetMapping("/reservation/{Id_reservation}")
    public ResponseEntity<?> getCheckInByReservation(@PathVariable Long Id_reservation) {
        try {
            System.out.println("===> Recherche Check-in pour réservation ID: " + Id_reservation);
            Check_In checkIn = checkInService.getCheckInByReservation(Id_reservation);
            if (checkIn != null) {
                CheckInDTO checkInDTO = CheckInMapper.toDTO(checkIn);
                return ResponseEntity.ok(checkInDTO);
            } else {
                System.out.println("===> checkIn is null, checking reservation existence...");
                boolean exists = reservationService.existsById(Id_reservation);
                System.out.println("===> Reservation exists? " + exists);
                if (reservationService.existsById(Id_reservation)) {
                    return ResponseEntity.ok("Check-in non encore effectué.");
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Réservation introuvable.");
                }
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }


    @GetMapping("/status/{Id_reservation}")
    public ResponseEntity<CheckInStatus> getStatusCheckIn(@PathVariable Long Id_reservation) {
        CheckInStatus status = checkInService.getStatusCheckIn(Id_reservation);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/validercheckinreception")
    public ResponseEntity<?> validercheckinReception(@RequestParam Long id_checkin) {
        checkInService.validerCheckinReception(id_checkin);
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
            @RequestParam Long id_reservation,
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

        DocumentScan documentScan = documentScanMapper.ToEntity(dto, null);
        checkInService.ajoutercheckinReception(id_reservation, documentScan);

        return ResponseEntity.ok("Check-in ajouté avec succès");
    }
}


