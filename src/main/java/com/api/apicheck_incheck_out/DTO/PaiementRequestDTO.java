package com.api.apicheck_incheck_out.DTO;

import com.api.apicheck_incheck_out.Enums.PaiementMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaiementRequestDTO {
    private Long reservationId;
    private String method;
    private String clientSecret;
}
