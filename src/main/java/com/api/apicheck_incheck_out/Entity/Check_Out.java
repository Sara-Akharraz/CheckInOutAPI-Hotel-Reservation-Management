package com.api.apicheck_incheck_out.Entity;


import com.api.apicheck_incheck_out.Enums.CheckOutStatut;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name="check_out")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Check_Out {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Reservation reservation;
    @Column(name="date_checkIn")
    private LocalDate dateCheckOut;
    @Enumerated(EnumType.STRING)
    private CheckOutStatut checkOutStatut;

}
