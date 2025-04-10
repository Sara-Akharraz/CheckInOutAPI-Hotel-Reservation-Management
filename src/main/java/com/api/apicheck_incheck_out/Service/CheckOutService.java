package com.api.apicheck_incheck_out.Service;

import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Enums.CheckOutStatut;

import java.util.List;

public interface CheckOutService {
    public Check_Out addCheckOut(Check_Out checkout);
    public Check_Out getCheckOutById(Long id);
    public List<Check_Out> getAllCheckOuts();
    public Check_Out setCheck_OutStatus(Long id,CheckOutStatut newStatus);
    public boolean isValidCheckOut(Long id);
    public double getTotalPrice(Long id);
}