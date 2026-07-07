package aperez.notificaciones.comandos;

import aperez.Comando;
import aperez.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ComandoPing implements Comando {

    public void ejecutar(ContextoComando ctx){
       ctx.responder("Pong");
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("ping", "Pong de respuesta del bot");
    }
}