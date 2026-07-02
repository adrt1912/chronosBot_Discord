package aperez578;

import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction; // 🌟 Clase clave de JDA

public class BotonesEventos {

    private static BotonesEventos instancia;

    // Constructor privado limpio
    private BotonesEventos() {}

    public static BotonesEventos getBotonesEventos() {
        if (instancia == null) instancia = new BotonesEventos();
        return instancia;
    }

    public void aplicarBotones(MessageCreateAction accion, Tarea tarea) {

        switch (tarea.getBotonesTipo()) {
            case 1: // 📅 BOTONES DE ASISTENCIA
                Button botonSi = Button.success("asistir_" + tarea.getId(), "✅ Asistiré");
                Button botonNo = Button.danger("desapuntarse_" + tarea.getId(), "❌ No asistiré");
                accion.addActionRow(botonSi, botonNo); // Se los acoplamos a la acción de envío
                break;
            case 2: // 📊 ENCUESTA CON OPCIONES RELLENABLES (DINÁMICAS)
                String opcionesRaw = tarea.getOpciones();

                // Si por algún motivo está vacío, ponemos opciones por defecto para que no rompa
                if (opcionesRaw == null || opcionesRaw.isEmpty()) opcionesRaw = "A|B|C|D";

                // Rompemos el texto por la barra vertical |
                String[] opciones = opcionesRaw.split("\\|");

                // Creamos una fila de componentes de Discord
                net.dv8tion.jda.api.interactions.components.LayoutComponent[] botones = new net.dv8tion.jda.api.interactions.components.LayoutComponent[opciones.length];

                java.util.List<Button> listaBotones = new java.util.ArrayList<>();

                for (int i = 0; i < opciones.length; i++) {
                    String nombreOpcion = opciones[i].trim();
                    // ID Secreto: "voto_[INDICE]_[ID_TAREA]" -> Ej: "voto_0_5" (Voto por la primera opción del evento 5)
                    String idSecreto = "voto_" + i + "_" + tarea.getId();
                    // Creamos el botón azul con el nombre real rellenado por el usuario
                    listaBotones.add(Button.primary(idSecreto, nombreOpcion));
                }
                // Enganchamos la lista de botones al mensaje
                accion.setActionRow(listaBotones);
                break;
            default:
                // Tipo 0: No hacemos nada, el mensaje se irá limpio sin botones
        }
    }
}