package com.analisys.gimnasio.clases_service.dto;

import java.time.LocalDateTime;

public class HorarioClaseRequest {

    private LocalDateTime horario;

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }
}
