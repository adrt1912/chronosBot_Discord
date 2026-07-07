package Aperez578.Notificaciones.Comandos;

import Aperez578.Comando;
import Aperez578.ContextoComando;
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

    @Override
    public void ejecutar(ContextoComando ctx) {
        //  Extraemos los parámetros nativos de Discord directamente
        int id = ctx.getParametroInt("id");
        boolean tieneTitulo = ctx.tieneOpcion("titulo");
        boolean tieneFecha = ctx.tieneOpcion("fecha_hora");

        if (!tieneTitulo && !tieneFecha) ctx.responder("⚠️ **Error:** Debes rellenar al menos uno de los campos opcionales (`titulo` o `fecha_hora`) para poder editar el evento.");
        else {
            EmbedBuilder embed = new EmbedBuilder().setTimestamp(java.time.Instant.now());
            StringBuilder descripcionCambios = new StringBuilder();
            boolean exitoModificacion = false;
            boolean huboErrorFecha = false;

            // MÓDULO 1: EDITAR TÍTULO
            if (tieneTitulo) {
                String nuevoTitulo = ctx.getParametroString("titulo").trim();
                if (NotificacionesBD.actualizarTitulo(id, nuevoTitulo)) {
                    descripcionCambios.append("📝 **Nuevo título:** ").append(nuevoTitulo).append("\n");
                    exitoModificacion = true;
                }
            }

            // MÓDULO 2: EDITAR FECHA Y HORA
            if (tieneFecha) {
                String fechaTexto = ctx.getParametroString("fecha_hora").trim();
                try {
                    LocalDateTime fechaConvertida = LocalDateTime.parse(fechaTexto, formateador);
                    long nuevoTimestamp = fechaConvertida.atZone(ZoneId.systemDefault()).toEpochSecond();

                    if (NotificacionesBD.actualizarTiempo(id, nuevoTimestamp)) {
                        // Aprovechamos tu formato premium de marcas de tiempo relativas
                        descripcionCambios.append("⏰ **Nuevo horario:** <t:").append(nuevoTimestamp).append(":F> (<t:").append(nuevoTimestamp).append(":R>)\n");
                        exitoModificacion = true;
                    }
                } catch (DateTimeParseException e) {
                    huboErrorFecha = true;
                }
            }

            //  GESTIÓN DE RESPUESTAS E INFORMES
            if (huboErrorFecha)
                ctx.responder("❌ **Fecha u hora inválida**. Usa el formato exacto: `DD/MM/YYYY HH:MM` (Ej: `15/07/2026 18:30`)");
            else {
                if (exitoModificacion) {
                    embed.setTitle("✅ Evento ID `" + id + "` Actualizado")
                            .setColor(Color.GREEN)
                            .setDescription("Se han aplicado los siguientes cambios con éxito:\n\n" + descripcionCambios);
                    ctx.responderEmbed(embed.build());
                } else {
                    embed.setTitle("❌ Error de Edición")
                            .setColor(Color.RED)
                            .setDescription("No se encontró ningún evento o encuesta activa con el ID `" + id + "` en la base de datos.");
                    ctx.responderEmbed(embed.build());
                }
            }
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return  Commands.slash("editar-notificacion", "Permite modificar un evento o encuesta existente")
                .addOptions(new OptionData(OptionType.INTEGER, "id", "El ID numérico del evento", true).setAutoComplete(true))
                .addOptions(new OptionData(OptionType.STRING, "titulo", "Nuevo título para el evento (Opcional)", false).setAutoComplete(true))
                .addOptions(new OptionData(OptionType.STRING, "fecha_hora", "Nueva fecha/hora: DD/MM/AAAA HH:MM (Opcional)", false).setAutoComplete(true));
    }
}