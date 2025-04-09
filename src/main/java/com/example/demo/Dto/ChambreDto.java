package com.example.demo.Dto;

import com.example.demo.Entity.User;
import com.example.demo.Enum.ChambreStatut;

public class ChambreDto {

    private Long id;
    private int etage;
    private ChambreStatut statut;
    private User assignedClient;
}