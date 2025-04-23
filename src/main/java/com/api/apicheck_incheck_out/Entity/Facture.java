package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enums.FactureType;
import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

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

    @Column(name="tax")
    private double tax;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private PaiementStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name="facture_type")
    private FactureType type;

    @ManyToOne
    @JoinColumn(name="reservation_id")
    @ToString.Exclude
    private Reservation reservation;

}
