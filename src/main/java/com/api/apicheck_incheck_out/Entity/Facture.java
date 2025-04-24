package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enums.FactureType;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="facture")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Facture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="checkInMontant", nullable = true)
    private double checkInMontant;
    @Column(name="checkOutMontant", nullable = true)
    private double checkOutMontant;
    @Column(name="tax", nullable = true)
    private double tax;
    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private PaiementStatus status;
    @Enumerated(EnumType.STRING)
    @Column(name="facture_type")
    private FactureType type;

    @ManyToOne
    @JoinColumn(name="reservation_id")
    private Reservation reservation;

}