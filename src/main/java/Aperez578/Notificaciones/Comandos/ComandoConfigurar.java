package Aperez578.Notificaciones.Comandos;

import Aperez578.Comando;
import Aperez578.ContextoComando;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ComandoConfigurar implements Comando {

    @Override
    public void ejecutar(ContextoComando ctx) {
        //  Buscamos el canal: si es '!', lo busca en el texto. Si es '/', busca la opción "canal"
        GuildChannel canalMencionado = ctx.getParametroCanal("canal");

        // Verificamos si el usuario se ha olvidado de poner el canal
        if (canalMencionado == null) {
            ctx.responder("""
                    ⚠️ **Uso incorrecto**. Debes indicar un canal válido:
                    🔹 Con texto: `!Configurar [#canal]` (Ej: `!Configurar #anuncios`)
                    🔹 Con barra: `/configuracion canal: [#canal]`""");
        } else {
            // Guardamos en la base de datos usando el ID del servidor y del canal desde el contexto 🌟
            NotificacionesBD.guardarCanalAlertas(ctx.getGuildId(), canalMencionado.getId());

            ctx.responder("✅ **Configuración guardada**. A partir de ahora, todas las alarmas automáticas se enviarán a " + canalMencionado.getAsMention());
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("configuracion", "Configura el canal de alertas global");
    }
}