package com.api.apicheck_incheck_out.Dto;


import com.api.apicheck_incheck_out.Entity.User;
import com.api.apicheck_incheck_out.Enum.ChambreStatut;

public class ChambreDto {

    private Long id;
    private int etage;
    private ChambreStatut statut;
    private User assignedClient;
}