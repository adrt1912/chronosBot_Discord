package aperez578.notificaciones.comandos;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

public record Tarea(
        String titulo,
        long timestamp,
        String userID,
        String canalID,
        int id,
        String rol_id,
        int botonesTipo,
        String opciones
) {
    // Campos calculados automáticos derivados del timestamp nativo del Record
    public LocalDate getFecha() {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()).toLocalDate();
    }

    public LocalTime getHora() {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault()).toLocalTime();
    }}