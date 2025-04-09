package com.api.apicheck_incheck_out.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="message")
    private String message;
    @Column(name="dateEnvoi")
    private LocalDate dateEnvoi;
    @ManyToOne
    @JoinColumn(name="user")
    private User user;
}
