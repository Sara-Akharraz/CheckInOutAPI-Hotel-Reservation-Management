package com.api.apicheck_incheck_out.entity;


import com.api.apicheck_incheck_out.enums.ReservationStatus;
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
    private LocalDate dateDebut;

    @Column(name="date_fin",nullable = false)
    private LocalDate dateFin;

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
    private CheckIn checkIn;

    @OneToOne(mappedBy = "reservation",cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private CheckOut checkOut;

    @OneToMany(mappedBy = "reservation",cascade = CascadeType.ALL)
    @ToString.Exclude
    @JsonManagedReference
    private List<ReservationServices> serviceList;
}
