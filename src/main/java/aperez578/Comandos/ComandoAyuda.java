package aperez578.Comandos;

import aperez578.Comando;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ComandoAyuda implements Comando {

    public void ejecutar(MessageReceivedEvent event) {

        String textAyuda="";
        event.getChannel().sendMessage(textAyuda).queue();

    }
}
