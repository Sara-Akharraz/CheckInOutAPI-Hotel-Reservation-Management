package com.api.apicheck_incheck_out.Controller;

import com.api.apicheck_incheck_out.Service.StripePayment.CheckOutRequest;
import com.api.apicheck_incheck_out.Service.StripePayment.StripeResponse;
import com.api.apicheck_incheck_out.Service.StripePayment.StripeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
public class CheckOutPaymentController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/payment")
    public ResponseEntity<StripeResponse> checkoutProducts(@RequestBody CheckOutRequest checkoutRequest) {
        StripeResponse stripeResponse = stripeService.checkoutServices(checkoutRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }
    @GetMapping("/payment/success")
    public String successPage() {
        return "success";  // this would be a view name, make sure you have a success.html file
    }

    @GetMapping("/payment/cancel")
    public String cancelPage() {
        return "cancel";  // similarly, make sure you have a cancel.html file if it's a view
    }
}
