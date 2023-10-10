package org.example.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
@Data
@Builder
public class Funko {
    private long id;
    private UUID cod;
    private long myId;
    private String nombre;
    private String modelo;
    private double precio;
    private LocalDate fechaLanzamiento;
    @Builder.Default
    private LocalDateTime created_at = LocalDateTime.now();
    @Builder.Default
    private LocalDateTime updated_at = LocalDateTime.now();
}
