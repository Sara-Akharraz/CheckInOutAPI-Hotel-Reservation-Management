package com.api.apicheck_incheck_out.entity;

import com.api.apicheck_incheck_out.enums.ChambreType;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    @Column(name="nom",nullable = false)
    private  String nom;

    @Column(name = "etage", nullable = false)
    private String etage;

    @Column(name="prix",nullable = false)
    private double prix;

    @Enumerated(EnumType.STRING)
    @Column(name="type",nullable = false)
    private ChambreType type;

    @Column(name="capacité")
    private int capacite;

    @OneToMany(mappedBy = "chambre", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<ChambreReservation> chambreReservations;

}
