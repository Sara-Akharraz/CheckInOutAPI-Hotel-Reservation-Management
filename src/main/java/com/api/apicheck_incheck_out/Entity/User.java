package com.api.apicheck_incheck_out.Entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.List;



@Entity
@Table(name="user")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name")
    private String nom;

    @Column(name="prenom")
    private String prenom;

    @Column(name="password")
    private String password;
    @Column(name="email")
    private String email;
    @Column(name="cin")
    private String cin;

    @Column(name="numeroPassport")
    private String numeroPassport;

    @Column(name = "stripe_id")
    private String stripeId;

    @Column(name="papal_id")
    private String paypalId;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;

}
