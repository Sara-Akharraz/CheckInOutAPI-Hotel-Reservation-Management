package com.example.demo.Entity;

import com.example.demo.Enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
@Entity
@Table(name="reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="client_id",nullable = false)
    private User client;

    @OneToMany(mappedBy = "reservation")
    private List<Chambre> chambreList;

    @Column(name="date_debut",nullable = false)
    private LocalDate date_debut;

    @Column(name="date_fin",nullable = false)
    private LocalDate date_fin;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ReservationStatus status;

    @OneToMany(mappedBy = "reservation",cascade = CascadeType.ALL)
    private List<Facture> factureList;
}
