package aperez578.Utilidad.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.Duration;
import java.util.Objects;

public class ComandoWarn implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        SlashCommandInteractionEvent event = ctx.getEventSlash();
        if (event.getGuild() != null) {
            // Solo los moderadores pueden usarlo (Permiso de expulsar o moderar miembros)
            if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.KICK_MEMBERS)) ctx.responder("❌ **Permisos Insuficientes** | Necesitas permisos de moderación para usar este comando.");
            else {
                Member objetivo = Objects.requireNonNull(event.getOption("usuario")).getAsMember();
                String razon = ctx.getParametroString("razon");

                if (objetivo == null) ctx.responder("❌ **Error** | No se ha podido encontrar a ese usuario en el servidor.");
                else if (objetivo.getId().equals(ctx.getIdAutor())) ctx.responder("🤨 ¿Te vas a poner un aviso a ti mismo? Buen intento, pero no.");
                else if (objetivo.getUser().isBot()) ctx.responder("🤖 Los bots somos seres perfectos, no rompemos las reglas.");
                else if (!event.getMember().canInteract(objetivo)) ctx.responder("❌ **Operación Cancelada** | No puedes amonestar a este usuario porque tiene un rol igual o superior al tuyo.");
                else {
                    String targetId = objetivo.getId();
                    String modId = ctx.getIdAutor();
                    String guildId = event.getGuild().getId();

                    // Guardamos en la BD y el metodo nos devuelve el total de avisos acumulados
                    int totalWarns = ConexionBD.getConexionBD().registrarAdvertencia(targetId, guildId, razon, modId);

                    StringBuilder respuesta = new StringBuilder();
                    respuesta.append("⚠️ **¡Usuario Advertido!** ⚠️\n")
                            .append("➔ **Usuario:** ").append(objetivo.getAsMention()).append("\n")
                            .append("➔ **Razón:** `").append(razon).append("`\n")
                            .append("➔ **Moderador:** <@").append(modId).append(">\n")
                            .append("➔ **Avisos totales:** `").append(totalWarns).append("/3`\n");

                    if (totalWarns >= 3) {
                        if (event.getGuild().getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
                            // Le metemos un timeout de 1 hora
                            objetivo.timeoutFor(Duration.ofHours(1)).reason("Acumulación de 3 advertencias").queue();
                            respuesta.append("\n🛑 **¡Castigo Aplicado!** El usuario ha alcanzado el límite de avisos y ha sido aislado durante 1 hora.");
                            ConexionBD.getConexionBD().resetarAdvertencias(targetId,guildId);
                        } else respuesta.append("\n⚠️ *El bot no tiene el permiso `Moderar Miembros` para aplicar el aislamiento automático.*");
                    }
                    ctx.responder(respuesta.toString());
                }
            }
        }
    }
}