package com.api.apicheck_incheck_out.Dto;

import com.api.apicheck_incheck_out.Entity.Reservation;
import com.api.apicheck_incheck_out.Enum.CheckOutStatut;
import jakarta.persistence.*;

public class CheckOutDTO {

    private Long id;
    private Long id_reservation;
    private CheckOutStatut checkOutStatut;
}
