package com.api.apicheck_incheck_out.Entity;


import com.api.apicheck_incheck_out.Enums.ChambreStatut;
import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id",nullable = false)
    @ToString.Exclude
    @JsonManagedReference
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ChambreReservation> chambreReservations;


    @Column(name="date_debut",nullable = false)
    private LocalDate date_debut;

    @Column(name="date_fin",nullable = false)
    private LocalDate date_fin;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private ReservationStatus status;

    @OneToMany(mappedBy = "reservation",cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private List<Facture> factureList;

    @OneToOne(mappedBy = "reservation",cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private Check_In checkIn;

    @OneToOne(mappedBy = "reservation",cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private Check_Out checkOut;

    @OneToMany(mappedBy = "reservation",cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private List<ReservationServices> serviceList;
}
