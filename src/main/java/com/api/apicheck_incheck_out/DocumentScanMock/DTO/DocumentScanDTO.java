package com.api.apicheck_incheck_out.DocumentScanMock.DTO;

import com.api.apicheck_incheck_out.Entity.Check_In;
import com.api.apicheck_incheck_out.Enums.DocumentScanType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentScanDTO {
    @NotNull
    Long id;
    @NotBlank
    private String nom;
    @NotBlank
    private String prenom;
    @NotEmpty
    private DocumentScanType type;

    private String cin;

    private String passport;

}
