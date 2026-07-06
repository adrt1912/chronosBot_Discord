package aperez578.Notificaciones.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;

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
        if (fraseComando.length < 5)
            ctx.responder("⚠️ **Uso incorrecto**. Debes escribir:\n`!CrearNotificacion [Nombre] [Fecha] [Hora] [@Rol/NINGUNA] [TipoBoton] [Opciones]`");
        else {

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
            ConexionBD conexionBD = ConexionBD.getConexionBD();

            int tipoBoton1 = 0;
            String opcionesTexto = "";

            // Si existe el parámetro del tipo de botón (Índice 5)
            if (fraseComando.length >= 6) {
                try {
                    tipoBoton1 = Integer.parseInt(fraseComando[5]);
                    if (tipoBoton1 < 0 || tipoBoton1 > 2) tipoBoton1 = 0;
                    if (tipoBoton1 == 2 && fraseComando.length >= 7)
                        opcionesTexto = fraseComando[6]; // Captura de forma segura todo lo que quede de texto

                } catch (NumberFormatException e) {
                    // Si no es un número, se queda en notificación normal
                }
            }
            boolean bien = conexionBD.crearTarea(nombreTarea, timestamp, idAutor, idCanal, mencionExtra, tipoBoton, opcionesTexto);

            String texto;
            if (bien) texto = "✅ Se ha guardado correctamente la tarea **" + nombreTarea + "** para el día " + fechaTarea + " a las " + horaTarea + " hs.";
            else texto = "❌ No se ha podido guardar, algo falló en la base de datos.";

            ctx.responder(texto);
        }
    }
}