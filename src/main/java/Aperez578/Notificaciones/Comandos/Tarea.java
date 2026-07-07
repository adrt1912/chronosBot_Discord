package Aperez578.Notificaciones.Comandos;

import java.time.*;

public class Tarea {
    private int id;
    private final String titulo;
    private final LocalDate fecha;
    private final LocalTime hora;
    private final String userID;
    private final String canalID;
    private final String rol_id;
    private final int botonesTipo;
    private final String opciones;
    private final long timestamp;

    public Tarea(String titulo, long timestamp, String userID, String canalID, int id, String rolId,int botonTipo,String opciones){
        this.titulo=titulo;
        // 1. Convertimos los segundos (long) en un instante en el tiempo
        Instant instante = Instant.ofEpochSecond(timestamp);

        // 2. Traducimos ese instante a la fecha y hora local de tu ordenador
        LocalDateTime fechaHoraCompleta = LocalDateTime.ofInstant(instante, ZoneId.systemDefault());

        // 3. Ahora sí, separamos el pastel y rellenamos tus atributos nativos
        this.fecha = fechaHoraCompleta.toLocalDate(); // Guarda el año-mes-día
        this.hora = fechaHoraCompleta.toLocalTime();
        this.timestamp=timestamp;
        this.userID=userID;
        this.canalID=canalID;
        this.id=id;
        this.rol_id=rolId;
        this.botonesTipo=botonTipo;
        this.opciones=opciones;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUserID() {
        return userID;
    }

    public String getCanalID() {
        return canalID;
    }

    public int getId() {
        return id;
    }

    public String getRol_id() {
        return rol_id;
    }

    public int getBotonesTipo() {
        return botonesTipo;
    }

    public String getOpciones() {
        return opciones;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Tienes la tarea "+titulo+" el dia "+fecha+" a las "+hora+" con id "+id;
    }
}