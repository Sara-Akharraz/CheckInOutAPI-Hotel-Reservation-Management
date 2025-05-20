package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import com.api.apicheck_incheck_out.Enums.PhaseAjoutService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="reservation_service")
public class ReservationServices {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="id_reservation")
    @JsonBackReference
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name="id_service")
    private Services service;

    @Enumerated(EnumType.STRING)
    private PhaseAjoutService phaseAjoutService;

    @Enumerated(EnumType.STRING)
    private PaiementStatus paiementStatus;
}
