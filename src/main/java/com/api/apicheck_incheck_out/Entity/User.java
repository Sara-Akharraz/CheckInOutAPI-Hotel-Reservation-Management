package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enum.Role;
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
    private String name;

    @Column(name="password")
    private String password;

    @Column(name="email", unique = true)
    private String email;

    @OneToMany(mappedBy = "user")
    private List<Notification> notifications;
    @Enumerated(EnumType.STRING)
    private Role role;
    @OneToMany(mappedBy = "client")
    private List<Reservation> reservations;

}
