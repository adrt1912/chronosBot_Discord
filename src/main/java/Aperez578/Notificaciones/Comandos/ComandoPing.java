package Aperez578.Notificaciones.Comandos;

import Aperez578.Comando;
import Aperez578.ContextoComando;
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