package aperez.experiencia.comandos;

import aperez.Comando;
import aperez.ContextoComando;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class ComandoTopXP implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        SlashCommandInteractionEvent event = ctx.getEventSlash();
        if (event.getGuild() == null) return;

        String guildId = event.getGuild().getId();
        List<String[]> topUsuarios = ExperienciaBD.obtenerTopNiveles(guildId);

        //  Filtro de seguridad por si el servidor es completamente nuevo
        if (topUsuarios.isEmpty())
            ctx.responder("🫙 **Clasificación Vacía** | Nadie ha empezado a hablar todavía en el servidor. ¡Rompe el hielo!");
        else {

            StringBuilder sb = new StringBuilder();
            sb.append("🏆 **TABLA DE CLASIFICACIÓN DE CHRONOS** 🏆\n");
            sb.append("Los 10 usuarios más activos y legendarios del servidor:\n");
            sb.append("---------------------------------------------------\n\n");

            String[] medallas = {"🥇", "🥈", "🥉"};

            // Recorremos la lista de los mejores usuarios
            for (int i = 0; i < topUsuarios.size(); i++) {
                String[] datos = topUsuarios.get(i);
                String userId = datos[0];
                String xp = datos[1];
                String nivel = datos[2];

                // Si es posición 1, 2 o 3 ponemos medalla. Si es mayor, ponemos el número estilizado (ej: #4).
                String posicionString = (i < 3) ? medallas[i] : "`#" + (i + 1) + "`";

                sb.append(posicionString)
                        .append(" <@").append(userId).append("> ➔ **Nivel ").append(nivel).append("** *(").append(xp).append(" XP)*\n");
            }

            sb.append("\n---------------------------------------------------\n");
            sb.append("💬 ¡Sigue chateando para escalar posiciones en el ranking!");

            ctx.responder(sb.toString());
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("topxp", "Muestra el Top 10 de los usuarios con más nivel y XP del servidor.");
    }
}