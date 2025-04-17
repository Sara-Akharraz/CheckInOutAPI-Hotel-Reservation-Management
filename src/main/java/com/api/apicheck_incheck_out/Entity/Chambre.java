package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ChambreType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chambre")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chambre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String Nom;
    @Column(name = "etage", nullable = false)
    private String etage;
    @Column(name="prix",nullable = false)
    private double prix;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private ChambreStatut statut;

    @ManyToOne
    @JoinColumn(name="reservation_id")
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(name="type",nullable = false)
    private ChambreType type;

    @Column(name="capacité")
    private int capacite;

}
