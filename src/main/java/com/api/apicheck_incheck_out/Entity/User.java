package com.api.apicheck_incheck_out.Entity;


import com.api.apicheck_incheck_out.Enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import jakarta.persistence.*;

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
    @Column(name="telephone")
    private String telephone;
    @Column(name="cin")
    private String cin;

    @Column(name="numeroPassport")
    private String numeroPassport;

    @Column(name = "stripe_id")
    private String stripeId;

    @Column(name="papal_id")
    private String paypalId;


    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Notification> notifications;

    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations;

    @Enumerated(EnumType.STRING)
    private Role role;

}
