package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Enums.CheckOutStatut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckOutDTO {

    private Long id;
    private Long id_reservation;
    private CheckOutStatut checkOutStatut;
}
