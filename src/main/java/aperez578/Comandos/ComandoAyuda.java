package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;

public class ComandoAyuda implements Comando {

    public void ejecutar(ContextoComando ctx) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📖 Panel de Ayuda • Chronos Bot")
                .setColor(new Color(0x3498db)) // Azul Chronos
                .setDescription("¡Hola! Aquí tienes la guía completa de comandos nativos. Ya no necesitas usar el prefijo `!`, ahora todos funcionan escribiendo **`/`** en el chat.\n\n" +
                        "📅 **GESTIÓN DE EVENTOS**\n" +
                        "🔹 `/crear-notificacion` ➜ Configura un nuevo evento o encuesta interactiva.\n" +
                        "🔹 `/editar-notificacion` ➜ Cambia el título o el horario de un evento activo.\n" +
                        "🔹 `/eliminar-tarea` ➜ Borra por completo un evento del sistema.\n" +
                        "🔹 `/cerrar` ➜ Finaliza una encuesta impidiendo que se registren más votos.\n\n" +
                        "🔍 **CONSULTAS Y AGENDAS**\n" +
                        "🔹 `/calendario` ➜ Explora los próximos eventos de forma paginada.\n" +
                        "🔹 `/mis-eventos` ➜ Revisa tu agenda personal (eventos donde marcaste Asistir).\n" +
                        "🔹 `/lista` ➜ Muestra la lista con nombres de los confirmados a un evento.\n" +
                        "🔹 `/listar-resultados` ➜ Visualiza el recuento definitivo de una encuesta cerrada.\n\n" +
                        "⚙️ **SISTEMA Y CONFIGURACIÓN**\n" +
                        "🔹 `/configuracion` ➜ Define el canal central a donde irán las alarmas automáticas.\n" +
                        "🔹 `/ping` ➜ Mide la velocidad de respuesta del bot.")
                .setFooter("Chronos Bot • Guía de Usuario", ctx.getJDA().getSelfUser().getAvatarUrl())
                .setTimestamp(java.time.Instant.now());

        ctx.responderEmbed(embed.build());
    }
}
