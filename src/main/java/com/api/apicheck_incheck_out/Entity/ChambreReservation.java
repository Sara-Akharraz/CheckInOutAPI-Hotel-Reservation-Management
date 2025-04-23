package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChambreReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name="chambre_id", nullable = false)
    private Chambre chambre;

    @Enumerated(EnumType.STRING)
    @Column(name="statut", nullable = false)
    private ChambreStatut statut;

}
