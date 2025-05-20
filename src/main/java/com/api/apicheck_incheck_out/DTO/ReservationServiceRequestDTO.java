package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Enums.PaiementStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationServiceRequestDTO {
        private Long reservationId;
        private Long serviceId;
        private String serviceName;
        private String serviceDescription;
        private Double servicePrice;
        private PaiementStatus paymentStatus;
}
