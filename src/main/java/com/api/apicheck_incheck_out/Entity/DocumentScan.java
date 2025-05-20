package com.api.apicheck_incheck_out.Entity;

import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Enums.DocumentScanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="documentScan")
public class DocumentScan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(name="nom")
    private String nom;
    @Column(name="prenom")
    private String prenom;
    @Column(name="cin")
    private String cin;
    @Column(name="passport")
    private String passport;
    @Enumerated(EnumType.STRING)
    @Column(name="type")
    private DocumentScanType type;

    @OneToOne(mappedBy = "documentScan")
    private Check_In checkIn;

    @Lob
    @Column(name="image",length = 10000000)
    private byte[] image;

    private String fileName;

    private String fileType;



}
