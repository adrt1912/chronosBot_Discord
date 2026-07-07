package aperez.experiencia.comandos;

import aperez.Comando;
import aperez.ContextoComando;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public class ComandoRank implements Comando {

    private static final String PARAM_USUARIO="usuario";

    @Override
    public void ejecutar(ContextoComando ctx) {
        SlashCommandInteractionEvent event = ctx.getEventSlash();
        if (event.getGuild() == null) return;

        // Si el usuario rellenó la opción, miramos a ese miembro. Si no, miramos al autor del comando.
        Member objetivo = event.getOption(PARAM_USUARIO) != null ? Objects.requireNonNull(event.getOption(PARAM_USUARIO)).getAsMember() : event.getMember();

        if (objetivo == null) ctx.responder("❌ **Error** | No se ha podido encontrar a ese usuario en el servidor.");
        else {
            if (objetivo.getUser().isBot()) ctx.responder("🤖 Los bots operamos más allá del sistema de experiencia mortal.");
            else {
                String targetId = objetivo.getId();
                String guildId = event.getGuild().getId();

                // Extraemos los datos de la base de datos
                int[] datosNivel = ExperienciaBD.obtenerPerfilNivel(targetId, guildId);
                int xpActual = datosNivel[0];
                int nivelActual = datosNivel[1];

                //  MATEMÁTICA DE LA BARRA DE PROGRESO (Sincronizada con tu fórmula de nivel)
                int xpBaseNivelAnterior = (nivelActual - 1) * 200;

                int xpGanadaEnEsteNivel = xpActual - xpBaseNivelAnterior;
                int xpTotalesRequeridosNivel = 200; // Cada nivel pide exactamente 200 XP en tu fórmula

                // Calculamos el porcentaje y cuántos bloques rellenar (Barra de 10 bloques)
                int porcentaje = (xpGanadaEnEsteNivel * 100) / xpTotalesRequeridosNivel;
                porcentaje = Math.clamp(porcentaje, 0, 100); // Asegura que esté entre 0 y 100
                int bloquesRellenos = porcentaje / 10;

                StringBuilder barraProgreso = new StringBuilder();
                for (int i = 0; i < 10; i++) {
                    if (i < bloquesRellenos) barraProgreso.append("▰");
                    else barraProgreso.append("▱");
                }

                //  DISEÑO DEL MENSAJE DE RANGO
                String sb = "✨ **TARJETA DE PROGRESO DE CHRONOS** ✨\n" +
                        "➔ **Usuario:** " + objetivo.getAsMention() + "\n" +
                        "---------------------------------------------------\n" +
                        "👑 **Nivel Actual:** ` Nivel " + nivelActual + " `\n" +
                        "🔮 **Experiencia:** `" + xpGanadaEnEsteNivel + " / " + xpTotalesRequeridosNivel + " XP` *(Total: " + xpActual + " XP)*\n\n" +
                        "📈 **Progreso hasta Nivel " + (nivelActual + 1) + ":**\n" +
                        "» `" + barraProgreso + "` [" + porcentaje + "%]\n" +
                        "---------------------------------------------------";

                ctx.responder(sb);
            }
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("rank", "Muestra tu nivel actual, experiencia y progreso en el servidor.")
                .addOption(OptionType.USER, PARAM_USUARIO, "El usuario del que quieres consultar el rango.", false);
    }
}