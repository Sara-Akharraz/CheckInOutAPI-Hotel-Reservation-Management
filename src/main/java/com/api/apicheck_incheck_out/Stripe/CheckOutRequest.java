package com.api.apicheck_incheck_out.Stripe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckOutRequest {
    private Long idCheckQOut;
    private String checkOutName;
    private Long amount;
    private Long quantity;
    private String currency;
}
