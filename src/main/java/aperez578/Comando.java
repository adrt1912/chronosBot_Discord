package aperez578;

import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface Comando {
    void ejecutar(ContextoComando ctx);
    SlashCommandData getDatosComando();
}