package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ComandoCrearNotificacion implements Comando {
    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void ejecutar(MessageReceivedEvent event) {
        String[] fraseComando = event.getMessage().getContentRaw().split("\\s+", 7);

        // Control de seguridad
        if (fraseComando.length < 5) {
            event.getChannel().sendMessage("⚠️ **Uso incorrecto**. Debes escribir:\n`!CrearNotificacion [Nombre] [Fecha] [Hora] [@Rol/NINGUNA] [TipoBoton] [Opciones]`").queue();
            return;
        }

        String nombreTarea = fraseComando[1];
        String fechaTarea = fraseComando[2];
        String horaTarea = fraseComando[3];
        String mencionExtra = fraseComando[4]; // Captura el "@Rol" o "NINGUNA"

        String fechaHoraTexto = fechaTarea + " " + horaTarea;
        LocalDateTime fechaConvertida;

        try {
            fechaConvertida = LocalDateTime.parse(fechaHoraTexto, formateador);
        } catch (DateTimeParseException e) {
            event.getChannel().sendMessage("❌ **Fecha u hora inválida**. Usa el formato: `DD/MM/YYYY HH:MM`").queue();
            return;
        }

        long timestamp = fechaConvertida.atZone(ZoneId.systemDefault()).toEpochSecond();
        String idAutor = event.getAuthor().getId();
        String idCanal = event.getChannel().getId();
        ConexionBD conexionBD = ConexionBD.getConexionBD();

        int tipoBoton = 0;
        String opcionesTexto = "";

        // Si existe el parámetro del tipo de botón (Índice 5)
        if (fraseComando.length >= 6) {
            try {
                tipoBoton = Integer.parseInt(fraseComando[5]);
                if (tipoBoton < 0 || tipoBoton > 2) tipoBoton = 0;
                if (tipoBoton == 2 && fraseComando.length >= 7) opcionesTexto = fraseComando[6]; // Captura de forma segura todo lo que quede de texto

            } catch (NumberFormatException e) {
                // Si no es un número, se queda en notificación normal
            }
        }

        boolean bien = conexionBD.crearTarea(nombreTarea, timestamp, idAutor, idCanal, mencionExtra, tipoBoton, opcionesTexto);

        String texto;
        if (bien) texto = "✅ Se ha guardado correctamente la tarea **" + nombreTarea + "** para el día " + fechaTarea + " a las " + horaTarea + " hs.";
         else texto = "❌ No se ha podido guardar, algo falló en la base de datos.";

        event.getChannel().sendMessage(texto).queue();
    }
}