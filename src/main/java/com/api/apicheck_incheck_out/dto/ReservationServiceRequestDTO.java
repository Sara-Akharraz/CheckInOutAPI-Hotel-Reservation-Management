package com.api.apicheck_incheck_out.dto;

import com.api.apicheck_incheck_out.enums.PaiementStatus;
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
