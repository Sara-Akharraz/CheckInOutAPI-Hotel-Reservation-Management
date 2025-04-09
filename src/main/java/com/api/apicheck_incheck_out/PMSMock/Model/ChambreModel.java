package com.api.apicheck_incheck_out.PMSMock.Model;

import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Enum.ChambreStatut;
import jakarta.persistence.Embeddable;

@Embeddable
public class ChambreModel {

    private Long id;
    private String name;
    private ChambreStatut statut;
    private Long id_assignedReservation;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ChambreStatut getStatut() {
        return statut;
    }
    public void setStatut(ChambreStatut statut) {
        this.statut = statut;
    }
    public Long getId_assignedReservation() {
        return id_assignedReservation;
    }
    public void setId_assignedReservation(Long id_assignedReservation) {
        this.id_assignedReservation = id_assignedReservation;
    }
}
