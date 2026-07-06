package aperez578.Notificaciones.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ComandoCrearNotificacion implements Comando {
    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void ejecutar(ContextoComando ctx) {
        String[] fraseComando;

            String titulo = ctx.getParametroString("titulo");

            // Separamos "DD/MM/AAAA HH:MM" en dos variables (Fecha y Hora)
            String[] fechaHora = ctx.getParametroString("fecha_hora").split(" ");
            String fecha = fechaHora[0];
            String hora = fechaHora.length > 1 ? fechaHora[1] : "00:00";

            String rol = ctx.getParametroRolMention("rol");
            int tipoBoton = ctx.getParametroInt("tipo_boton");
            String opciones = ctx.getParametroString("opciones");

            if (opciones != null) {
                // Si hay opciones, creamos el array completo de 7 posiciones
                fraseComando = new String[]{"!CrearNotificacion", titulo, fecha, hora, rol, String.valueOf(tipoBoton), opciones};
            } else {
                // Si no hay opciones, lo dejamos en 6 posiciones
                fraseComando = new String[]{"!CrearNotificacion", titulo, fecha, hora, rol, String.valueOf(tipoBoton)};
            }

        // Control de seguridad
        String nombreTarea = fraseComando[1];
        String fechaTarea = fraseComando[2];
        String horaTarea = fraseComando[3];
        String mencionExtra = fraseComando[4]; // Captura el "@Rol" o "NINGUNA"

        String fechaHoraTexto = fechaTarea + " " + horaTarea;
        LocalDateTime fechaConvertida;

        try {
            fechaConvertida = LocalDateTime.parse(fechaHoraTexto, formateador);
        } catch (DateTimeParseException e) {
           ctx.responder("❌ **Fecha u hora inválida**. Usa el formato: `DD/MM/YYYY HH:MM`");
            return;
        }

        long timestamp = fechaConvertida.atZone(ZoneId.systemDefault()).toEpochSecond();
        String idAutor = ctx.getIdAutor();
        String idCanal = ctx.getChanelId();

        int tipoBoton1;
        String opcionesTexto = "";

        // Si existe el parámetro del tipo de botón (Índice 5)
        try {
            tipoBoton1 = Integer.parseInt(fraseComando[5]);
            if (tipoBoton1 < 0 || tipoBoton1 > 2) tipoBoton1 = 0;
            if (tipoBoton1 == 2 && fraseComando.length == 7)
                opcionesTexto = fraseComando[6]; // Captura de forma segura todo lo que quede de texto

        } catch (NumberFormatException e) {
            // Si no es un número, se queda en notificación normal
        }
        boolean bien = NotificacionesBD.crearTarea(nombreTarea, timestamp, idAutor, idCanal, mencionExtra, tipoBoton, opcionesTexto);

        String texto;
        if (bien) texto = "✅ Se ha guardado correctamente la tarea **" + nombreTarea + "** para el día " + fechaTarea + " a las " + horaTarea + " hs.";
        else texto = "❌ No se ha podido guardar, algo falló en la base de datos.";

        ctx.responder(texto);
    }

    @Override
    public SlashCommandData getDatosComando() {
        return  Commands.slash("crear-notificacion", "Crea un nuevo evento o encuesta")
                .addOption(OptionType.STRING, "titulo", "El título del evento", true)
                .addOption(OptionType.STRING, "fecha_hora", "Formato: DD/MM/AAAA HH:MM (Ej: 15/07/2026 18:00)", true)
                .addOptions(
                        new OptionData(OptionType.INTEGER, "tipo_boton", "Elige el formato interactivo", true)
                                .addChoice("📅 Lista de Asistencia", 1)
                                .addChoice("📊 Encuesta de Opciones", 2)
                                .addChoice("❌ Sin Botones (Solo aviso)", 0)
                )
                .addOption(OptionType.ROLE, "rol", "Rol al que quieres hacer ping (Opcional)", false)
                .addOption(OptionType.STRING, "opciones", "Para encuestas. Separa con barras: Opción A | Opción B (Opcional)", false);
    }
}