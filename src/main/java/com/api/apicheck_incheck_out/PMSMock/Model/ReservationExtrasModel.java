package com.api.apicheck_incheck_out.PMSMock.Model;

import java.util.List;

public class ReservationExtrasModel {
    private Long id_reservation;
    private List<ExtraModel> extras;

    public Long getId_reservation() {
        return id_reservation;
    }
    public void setId_reservation(Long id_reservation) {
        this.id_reservation = id_reservation;
    }

    public List<ExtraModel> getExtras() {
        return extras;
    }
    public void setExtras(List<ExtraModel> extras) {
        this.extras = extras;
    }
}
