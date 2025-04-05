package com.example.demo.Entity;

import com.example.demo.Enums.ChambreStatut;
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

    @Column(name = "etage", nullable = false)
    private int etage;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private ChambreStatut statut;

}
