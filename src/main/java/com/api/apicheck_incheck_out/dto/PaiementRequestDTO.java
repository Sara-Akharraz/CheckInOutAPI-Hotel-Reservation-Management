package com.api.apicheck_incheck_out.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaiementRequestDTO {
    private Long reservationId;
    private String method;
    private String clientSecret;
}
