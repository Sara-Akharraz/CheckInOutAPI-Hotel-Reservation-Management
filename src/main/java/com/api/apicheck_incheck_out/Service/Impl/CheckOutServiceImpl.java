package com.api.apicheck_incheck_out.Service.Impl;

import com.api.apicheck_incheck_out.Entity.Check_Out;
import com.api.apicheck_incheck_out.Enums.CheckOutStatut;
import com.api.apicheck_incheck_out.PMSMock.Service.Impl.ExtraMockImpl;
import com.api.apicheck_incheck_out.Repository.CheckOutRepository;
import com.api.apicheck_incheck_out.Service.CheckOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CheckOutServiceImpl implements CheckOutService {

    @Autowired
    CheckOutRepository checkOutRepository;
    @Autowired
    private ExtraMockImpl extraMockImpl;

    public Check_Out addCheckOut(Check_Out checkout) {
        if (checkout == null) {
            throw new IllegalArgumentException("Check_Out object cannot be null");
        }
        checkout.setCheckOutStatut(CheckOutStatut.En_Attente);
        return checkOutRepository.save(checkout);
    }

    @Override
    public Check_Out getCheckOutById(Long id) {
        Optional<Check_Out> checkout = checkOutRepository.findById(id);
        if (checkout.isPresent()) {
            return checkout.get();
        } else {
            throw new RuntimeException("Check Out not found with id: " + id);
        }
    }

    @Override
    public List<Check_Out> getAllCheckOuts() {
        return checkOutRepository.findAll();
    }

    @Override
    public Check_Out setCheck_OutStatus(Long id, CheckOutStatut newStatus) {
        Check_Out checkout = checkOutRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Check Out not found with id: " + id));
        checkout.setCheckOutStatut(newStatus);
        return checkOutRepository.save(checkout);
    }

    @Override
    public boolean isValidCheckOut(Long id) {
        Check_Out checkout = checkOutRepository.findById(id)
                .orElseThrow( () -> new RuntimeException("Check Out not found with id: " + id));
        Long id_reservation = checkout.getReservation().getId();
        return extraMockImpl.calculateTotalExtraPrice(id_reservation)==0;
    }

    @Override
    public double getTotalPrice(Long id) {
        return extraMockImpl.calculateTotalExtraPrice(id);
    }

}