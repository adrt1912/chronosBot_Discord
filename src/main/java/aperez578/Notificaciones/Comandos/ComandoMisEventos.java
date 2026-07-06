package aperez578.Notificaciones.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;
import java.util.List;

public class ComandoMisEventos implements Comando {

    @Override
    public void ejecutar(ContextoComando ctx) {
        String userId = ctx.getIdAutor();

        // 🔍 Buscamos en la base de datos los eventos donde este usuario está apuntado
        List<Tarea> misEventos = ConexionBD.getConexionBD().listarTareasAsistidas(userId);

        // 🛑 CASO A: Si la agenda está totalmente vacía
        if (misEventos == null || misEventos.isEmpty()) {
            EmbedBuilder embedVacio = new EmbedBuilder()
                    .setTitle("📅 Tu Agenda Personal")
                    .setColor(Color.GRAY)
                    .setDescription("¡Hola <@" + userId + ">! Actualmente no estás apuntado a ningún evento.\n\n" +
                            "💡 *Pásate por el `/calendario` y pulsa **Asistir** en los eventos que te interesen.*");

            ctx.responderEmbed(embedVacio.build());
            return;
        }

        // 🟢 CASO B: Si el usuario sí tiene eventos guardados
        StringBuilder sb = new StringBuilder();
        sb.append("📅 **TU AGENDA PERSONAL DE EVENTOS** 📅\n");
        sb.append("Hola <@").append(userId).append(">, aquí tienes tus próximas actividades:\n\n");

        for (Tarea tarea : misEventos) {
            long ts = tarea.getTimestamp();
            sb.append("📌 **").append(tarea.getTitulo()).append("**\n")
                    .append("🆔 ID: `").append(tarea.getId()).append("` | ⏰ Horario: <t:").append(ts).append(":F> (<t:").append(ts).append(":R>)\n")
                    .append("───────────────────\n");
        }

        // Enviamos la lista formateada de una sola vez editando el "Pensando..."
        ctx.responder(sb.toString());
    }
}