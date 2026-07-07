package aperez578.Notificaciones.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.Color;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ComandoEditarNotificacion implements Comando {

    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String PARAM_FECHAHORA="fecha_hora";

    @Override
    public void ejecutar(ContextoComando ctx) {
        // Extraemos los parámetros nativos de Discord directamente
        int id = ctx.getParametroInt("id");
        boolean tieneTitulo = ctx.tieneOpcion("titulo");
        boolean tieneFecha = ctx.tieneOpcion(PARAM_FECHAHORA);

        if (!tieneTitulo && !tieneFecha) ctx.responder("⚠️ **Error:** Debes rellenar al menos uno de los campos opcionales (`titulo` o `fecha_hora`) para poder editar el evento.");
         else {
            StringBuilder descripcionCambios = new StringBuilder();
            boolean exitoModificacion = false;

            // MÓDULO 1: EDITAR TÍTULO
            if (tieneTitulo) {
                String nuevoTitulo = ctx.getParametroString("titulo").trim();
                if (NotificacionesBD.actualizarTitulo(id, nuevoTitulo)) {
                    descripcionCambios.append("📝 **Nuevo título:** ").append(nuevoTitulo).append("\n");
                    exitoModificacion = true;
                }
            }

            // MÓDULO 2: EDITAR FECHA Y HORA
            boolean huboErrorFecha = false;
            if (tieneFecha) {
                Long nuevoTimestamp = intentarObtenerTimestamp(ctx);
                if (nuevoTimestamp == null) huboErrorFecha = true;
                else if (NotificacionesBD.actualizarTiempo(id, nuevoTimestamp)) {
                    descripcionCambios.append("⏰ **Nuevo horario:** <t:").append(nuevoTimestamp).append(":F> (<t:").append(nuevoTimestamp).append(":R>)\n");
                    exitoModificacion = true;
                }
            }

            // GESTIÓN DE RESPUESTAS E INFORMES
            responderResultado(ctx, id, huboErrorFecha, exitoModificacion, descripcionCambios);
        }
    }

    private Long intentarObtenerTimestamp(ContextoComando ctx) {
        try {
            String fechaTexto = ctx.getParametroString(PARAM_FECHAHORA).trim();
            LocalDateTime fechaConvertida = LocalDateTime.parse(fechaTexto, formateador);
            return fechaConvertida.atZone(ZoneId.systemDefault()).toEpochSecond();
        } catch (DateTimeParseException _) {
            return null;
        }
    }

    private void responderResultado(ContextoComando ctx, int id, boolean huboErrorFecha, boolean exitoModificacion, StringBuilder descripcionCambios) {
        if (huboErrorFecha) ctx.responder("❌ **Fecha u hora inválida**. Usa el formato exacto: `DD/MM/YYYY HH:MM` (Ej: `15/07/2026 18:30`)");
        else{
        EmbedBuilder embed = new EmbedBuilder().setTimestamp(java.time.Instant.now());

        if (exitoModificacion) {
            embed.setTitle("✅ Evento ID `" + id + "` Actualizado")
                    .setColor(Color.GREEN)
                    .setDescription("Se han aplicado los siguientes cambios con éxito:\n\n" + descripcionCambios);
        } else {
            embed.setTitle("❌ Error de Edición")
                    .setColor(Color.RED)
                    .setDescription("No se encontró ningún evento o encuesta activa con el ID `" + id + "` en la base de datos.");
        }
        ctx.responderEmbed(embed.build());
    }
}

    @Override
    public SlashCommandData getDatosComando() {
        return  Commands.slash("editar-notificacion", "Permite modificar un evento o encuesta existente")
                .addOptions(new OptionData(OptionType.INTEGER, "id", "El ID numérico del evento", true).setAutoComplete(true))
                .addOptions(new OptionData(OptionType.STRING, "titulo", "Nuevo título para el evento (Opcional)", false).setAutoComplete(true))
                .addOptions(new OptionData(OptionType.STRING, PARAM_FECHAHORA, "Nueva fecha/hora: DD/MM/AAAA HH:MM (Opcional)", false).setAutoComplete(true));
    }
}