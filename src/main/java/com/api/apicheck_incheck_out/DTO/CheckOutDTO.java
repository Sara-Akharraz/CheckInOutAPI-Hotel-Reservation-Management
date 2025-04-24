package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Enum.CheckOutStatut;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CheckOutDTO {
    private Long id;
    private Long id_reservation;
    private CheckOutStatut checkOutStatut;
}