package com.api.apicheck_incheck_out.Entity;


import com.api.apicheck_incheck_out.Enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private User user;

    @OneToMany(mappedBy = "reservation",cascade = CascadeType.ALL)
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

    @OneToOne(mappedBy = "reservation")
    private Check_In checkIn;

    @OneToOne(mappedBy = "reservation")
    private Check_Out checkOut;
}
