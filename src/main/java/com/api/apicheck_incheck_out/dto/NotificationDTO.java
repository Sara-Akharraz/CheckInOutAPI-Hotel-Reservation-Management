package com.api.apicheck_incheck_out.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
    @NotNull
    private Long id;
    @NotBlank
    private String message;
    private LocalDate dateEnvoi;
    @NotNull
    private Long userId;
}
