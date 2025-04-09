package com.api.apicheck_incheck_out.Entity;


import com.api.apicheck_incheck_out.Enum.CheckOutStatut;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Enumerated(EnumType.STRING)
    private CheckOutStatut checkOutStatut;

}
