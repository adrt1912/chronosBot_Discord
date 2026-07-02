package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

public class ComandoConfigurar implements Comando {
    @Override
    public void ejecutar(MessageReceivedEvent event) {
        // Verificamos si el usuario mencionó un canal
        if (event.getMessage().getMentions().getChannels().isEmpty()) {
            event.getChannel().sendMessage("⚠️ **Uso correcto:** `!Configurar [#canal]` (Ej: `!Configurar #anuncios`)").queue();
            return;
        }

        // Capturamos el canal mencionado
        TextChannel canalMencionado = (TextChannel) event.getMessage().getMentions().getChannels().getFirst();

        // Guardamos en la base de datos usando el ID del servidor y del canal
        ConexionBD.getConexionBD().guardarCanalAlertas(event.getGuild().getId(), canalMencionado.getId());

        event.getChannel().sendMessage("✅ **Configuración guardada**. A partir de ahora, todas las alarmas automáticas se enviarán a " + canalMencionado.getAsMention()).queue();
    }
}