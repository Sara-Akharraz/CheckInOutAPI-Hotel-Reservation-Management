package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="facture")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="checkInMontant")
    private double checkInMontant;
    @Column(name="checkOutMontant")
    private double checkOutMontant;
    @Column(name="tva")
    private double tva;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private PaiementStatus status;
    @ManyToOne
    @JoinColumn(name="reservation_id")
    private Reservation reservation;
}
