package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ComandoEditarNotificacion implements Comando {

    private final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void ejecutar(MessageReceivedEvent event) {
        String[] comando = event.getMessage().getContentRaw().split(" ");
        ConexionBD conexionBD = ConexionBD.getConexionBD();

        if (comando.length < 4) {
            event.getChannel().sendMessage("""
                    ⚠️ **Uso incorrecto del comando**.
                    🔹 `!editar [ID] titulo [Nuevo Nombre]`
                    🔹 `!editar [ID] fecha [DD/MM/YYYY] [HH:MM]`""").queue();
        } else {

            try {
                // Ahora que sabemos que el comando es largo, es seguro extraer los datos
                int comandId = Integer.parseInt(comando[1]);
                String parametroEditar = comando[2].toLowerCase();

                EmbedBuilder embed = new EmbedBuilder().setTimestamp(java.time.Instant.now());

                // 📅 CASO 1: EDITAR FECHA
                if (parametroEditar.equals("fecha")) {
                    if (comando.length < 5)
                        event.getChannel().sendMessage("⚠️ Falta la hora. Ejemplo: `!editar " + comandId + " fecha 15/07/2026 18:30`").queue();
                    else {
                        String fechaTexto = comando[3] + " " + comando[4];
                        try {
                            LocalDateTime fechaConvertida = LocalDateTime.parse(fechaTexto, formateador);
                            long nuevoTimestamp = fechaConvertida.atZone(ZoneId.systemDefault()).toEpochSecond();

                            if (conexionBD.actualizarTiempo(comandId, nuevoTimestamp)) {
                                embed.setTitle("✅ Evento Editado")
                                        .setColor(Color.GREEN)
                                        .setDescription("La tarea con ID `" + comandId + "` ahora tiene la fecha:\n**" + comando[3] + " a las " + comando[4] + " hs**");
                            } else
                                embed.setTitle("❌ Error").setColor(Color.RED).setDescription("No se encontró ninguna tarea con el ID `" + comandId + "`.");

                            event.getChannel().sendMessageEmbeds(embed.build()).queue();

                        } catch (DateTimeParseException e) {
                            event.getChannel().sendMessage("❌ **Fecha u hora inválida**. Usa el formato: `DD/MM/YYYY HH:MM`").queue();
                        }
                    }
                }
                //  CASO 2: EDITAR TÍTULO
                else if (parametroEditar.equals("titulo")) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 3; i < comando.length; i++) {
                        sb.append(comando[i]).append(" ");
                    }
                    String nuevoTitulo = sb.toString().trim();

                    if (conexionBD.actualizarTitulo(comandId, nuevoTitulo)) {
                        embed.setTitle("✅ Evento Editado")
                                .setColor(Color.GREEN)
                                .setDescription("La tarea con ID `" + comandId + "` ahora se llama:\n**" + nuevoTitulo + "**");
                    } else
                        embed.setTitle("❌ Error").setColor(Color.RED).setDescription("No se encontró ninguna tarea con el ID `" + comandId + "`.");

                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }

                // CASO 3: PARÁMETRO INCORRECTO
                else {
                    event.getChannel().sendMessage("⚠️ Campo desconocido. Solo puedes editar `titulo` o `fecha`.").queue();
                }

            } catch (NumberFormatException e) {
                event.getChannel().sendMessage("❌ El ID de la tarea debe ser un número entero válido.").queue();
            }
        }
    }
}