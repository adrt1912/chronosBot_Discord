package aperez578.Comandos;

import aperez578.Comando;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ComandoPing implements Comando {

    public void ejecutar(MessageReceivedEvent event){
        event.getChannel().sendMessage("Pong").queue();
    }
}
