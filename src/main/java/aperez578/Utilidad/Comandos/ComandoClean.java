package aperez578.Utilidad.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class ComandoClean implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        SlashCommandInteractionEvent event = ctx.getEventSlash();
        if (event.getGuild() != null) {

            if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MESSAGE_MANAGE))
                ctx.responder("❌ **Permisos Insuficientes** | Necesitas el permiso de `Gestionar Mensajes` para usar este comando.");
            else {
                if (!event.getGuild().getSelfMember().hasPermission(Permission.MESSAGE_MANAGE)) {
                    ctx.responder("❌ **Error del Bot** | Me falta el permiso de `Gestionar Mensajes` en este servidor.");
                } else {
                    int cantidad = ctx.getParametroInt("cantidad");

                    if (cantidad < 1 || cantidad > 200)
                        ctx.responder("❌ **Cantidad Inválida** | Solo puedes borrar entre 1 y 100 mensajes a la vez.");
                    else {

                        event.getGuildChannel().getHistory().retrievePast(cantidad).queue(mensajes -> {
                            event.getGuildChannel().purgeMessages(mensajes);
                            event.getHook().sendMessage("🧹 **¡Limpieza Completada!** | Se han eliminado `" + mensajes.size() + "` mensajes con éxito.").queue();
                        }, _ -> event.getHook().sendMessage("❌ Hubo un error al intentar recuperar los mensajes del historial.").queue());
                    }
                }
            }
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("clean", "Borra una cantidad determinada de mensajes del canal actual.")
                .addOption(OptionType.INTEGER, "cantidad", "Número de mensajes a borrar (1-100).", true);
    }
}