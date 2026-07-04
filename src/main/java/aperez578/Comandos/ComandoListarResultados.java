package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;
import aperez578.Tarea;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.*;
import java.util.List;

public class ComandoListarResultados implements Comando {

    @Override
    public void ejecutar(ContextoComando ctx) {
        try {
            int idTarea = ctx.getParametroInt("id");

            Tarea tarea = ConexionBD.getConexionBD().obtenerTareaPorId(idTarea);
            if (tarea == null) ctx.responder("❌ No se ha encontrado ningún evento activo con el ID `" + idTarea + "`.");
            else {
                List<String> asistentesId = ConexionBD.getConexionBD().obtenerAsistentes(idTarea);

                // Reconstruimos tu preciosa tarjeta azul
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle("📝 Lista de Asistentes")
                        .setColor(new Color(0x3498db))
                        .setDescription("Aquí tienes el listado de usuarios confirmados para el evento:")
                        .addField("📌 Evento:", "**" + tarea.getTitulo() + "** (ID: `" + idTarea + "`)", false)
                        .setFooter("Chronos Control", ctx.getJDA().getSelfUser().getAvatarUrl());

                if (asistentesId.isEmpty()) embed.addField("👥 Usuarios Confirmados (0):", "*Nadie se ha apuntado todavía a este evento. ¡Sé el primero!*", false);
                else {
                    StringBuilder sb = new StringBuilder();
                    int contador = 1;
                    for (String userId : asistentesId) {
                        sb.append("`").append(contador).append(".` <@").append(userId).append(">\n");
                        contador++;
                    }
                    embed.addField("👥 Usuarios Confirmados (" + asistentesId.size() + "):", sb.toString(), false);
                }

                ctx.responderEmbed(embed.build());
            }
        } catch (Exception e) {
            // Si el usuario usa '!' y se olvida el número o escribe letras, este catch lo atrapa elegantemente
            ctx.responder("❌ **Error:** Debes indicar un ID numérico de evento válido.\n🔹 Uso con prefijo: `!Lista [ID]` \n🔹 Uso con barra: `/lista id: [ID]`");
        }
    }
}