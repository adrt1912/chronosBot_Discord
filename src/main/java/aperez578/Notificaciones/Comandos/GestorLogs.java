package aperez578.Notificaciones.Comandos;

import aperez578.ContextoComando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import java.awt.Color;
import java.time.Instant;

public class GestorLogs {

    public static void enviarLog(ContextoComando ctx, String tituloAccion, String detalles) {
        String guildId = ctx.getGuildId();

        String canalLogId = NotificacionesBD.obtenerCanalAlertas(guildId);

        // Si el servidor no ha configurado ningún canal de alertas todavía, ignoramos el log
        if (canalLogId != null && !canalLogId.isEmpty()) {

            // Buscamos el canal en el servidor de Discord
            TextChannel canal = ctx.getJDA().getTextChannelById(canalLogId);
            if (canal != null) { // Si el bot no lo encuentra o no tiene permisos, salimos}

                // Construimos el reporte gráfico de auditoría
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("⚠️ [AUDITORÍA] " + tituloAccion)
                        .setColor(Color.ORANGE) // Color naranja de advertencia
                        .setDescription(detalles)
                        .addField("👤 Moderador/Autor:", "<@" + ctx.getIdAutor() + ">", true)
                        .addField("📅 Fecha del reporte:", "<t:" + (Instant.now().getEpochSecond()) + ":F>", false)
                        .setFooter("Chronos Audit System", ctx.getJDA().getSelfUser().getAvatarUrl())
                        .setTimestamp(Instant.now());

                // Enviamos el log de forma silenciosa al canal de administración
                canal.sendMessageEmbeds(embed.build()).queue();
            }
        }
    }
}