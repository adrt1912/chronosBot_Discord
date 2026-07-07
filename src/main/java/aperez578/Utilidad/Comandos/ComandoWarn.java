package aperez578.Utilidad.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.Duration;
import java.util.Objects;

public class ComandoWarn implements Comando {

    @Override
    public void ejecutar(ContextoComando ctx) {
        SlashCommandInteractionEvent event = ctx.getEventSlash();
        if (event.getGuild() != null) {

            Member moderador = Objects.requireNonNull(event.getMember());
            Member objetivo = Objects.requireNonNull(event.getOption("usuario")).getAsMember();

            // 1. Cláusulas de guarda para validar permisos y objetivos
            if (!moderador.hasPermission(Permission.KICK_MEMBERS))
                ctx.responder("❌ **Permisos Insuficientes** | Necesitas permisos de moderación para usar este comando.");
            else if (objetivo == null)
                ctx.responder("❌ **Error** | No se ha podido encontrar a ese usuario en el servidor.");
            else if (objetivo.getId().equals(ctx.getIdAutor()))
                ctx.responder("🤨 ¿Te vas a poner un aviso a ti mismo? Buen intento, pero no.");
            else if (objetivo.getUser().isBot())
                ctx.responder("🤖 Los bots somos seres perfectos, no rompemos las reglas.");
            else if (!moderador.canInteract(objetivo))
                ctx.responder("❌ **Operación Cancelada** | No puedes amonestar a este usuario porque tiene un rol igual o superior al tuyo.");
            else {
                // 2. Procesamiento de la advertencia en la Base de Datos
                String targetId = objetivo.getId();
                String modId = ctx.getIdAutor();
                String guildId = event.getGuild().getId();
                String razon = ctx.getParametroString("razon");

                int totalWarns = UtilidadBD.registrarAdvertencia(targetId, guildId, razon, modId);

                // 3. Construcción del mensaje de respuesta
                StringBuilder respuesta = new StringBuilder();
                respuesta.append("⚠️ **¡Usuario Advertido!** ⚠️\n")
                        .append("➔ **Usuario:** ").append(objetivo.getAsMention()).append("\n")
                        .append("➔ **Razón:** `").append(razon).append("`\n")
                        .append("➔ **Moderador:** <@").append(modId).append(">\n")
                        .append("➔ **Avisos totales:** `").append(totalWarns).append("/3`\n");

                // 4. Evaluar castigo acumulativo si llega al límite
                if (totalWarns >= 3) evaluarYAplicarCastigo(event, objetivo, targetId, guildId, respuesta);
                ctx.responder(respuesta.toString());
            }
        }
    }
    // Metodo auxiliar para aislar las condicionales del castigo de 3 warns
    private void evaluarYAplicarCastigo(SlashCommandInteractionEvent event, Member objetivo, String targetId, String guildId, StringBuilder respuesta) {
        if (Objects.requireNonNull(event.getGuild()).getSelfMember().hasPermission(Permission.MODERATE_MEMBERS)) {
            // Le metemos un timeout de 1 hora
            objetivo.timeoutFor(Duration.ofHours(1)).reason("Acumulación de 3 advertencias").queue();
            respuesta.append("\n🛑 **¡Castigo Aplicado!** El usuario ha alcanzado el límite de avisos y ha sido aislado durante 1 hora.");
            UtilidadBD.resetarAdvertencias(targetId, guildId);
        } else respuesta.append("\n⚠️ *El bot no tiene el permiso `Moderar Miembros` para aplicar el aislamiento automático.*");
    }

    @Override
    public SlashCommandData getDatosComando() {
        return  Commands.slash("warn", "Amonesta a un usuario del servidor registrando una advertencia.")
                .addOption(OptionType.USER, "usuario", "El usuario al que quieres amonestar.", true)
                .addOption(OptionType.STRING, "razon", "El motivo del aviso.", true);
    }
}