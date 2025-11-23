package com.api.apicheck_incheck_out.dto;

import com.api.apicheck_incheck_out.enums.CheckOutStatut;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckOutDTO {

    private Long id;
    private Long idReservation;
    private CheckOutStatut checkOutStatut;
}
