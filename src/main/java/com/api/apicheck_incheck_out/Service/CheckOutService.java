package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Enum.CheckOutStatut;
import com.api.apicheck_incheck_out.Service.StripePayment.CheckOutRequest;
import com.api.apicheck_incheck_out.Service.StripePayment.StripeResponse;

import java.util.List;

public interface CheckOutService {
    public Check_Out addCheckOut(Check_Out checkout);
    public Check_Out getCheckOutById(Long id);
    public List<Check_Out> getAllCheckOuts();
    public Check_Out setCheck_OutStatus(Long id,CheckOutStatut newStatus);
    public boolean isValidCheckOut(Long id);
    public Long getAmount(Long id);
    public StripeResponse payer(Long id);
}