package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Enums.CheckInStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="check_in")
public class Check_In {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="date_checkIn")
    private LocalDate dateCheckIn;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private CheckInStatus status;

    @OneToOne
    @JoinColumn(name="reservation_id")
    @JsonBackReference
    private Reservation reservation;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="document_scan_id")
    private DocumentScan documentScan;
}
