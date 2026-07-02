package aperez578;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface Comando {
    void ejecutar(MessageReceivedEvent event);
}
