package aperez578.Notificaciones.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;

public class ComandoConfigurar implements Comando {

    @Override
    public void ejecutar(ContextoComando ctx) {
        // 🌟 Buscamos el canal: si es '!', lo busca en el texto. Si es '/', busca la opción "canal"
        GuildChannel canalMencionado = ctx.getParametroCanal("canal");

        // Verificamos si el usuario se ha olvidado de poner el canal
        if (canalMencionado == null) {
            ctx.responder("⚠️ **Uso incorrecto**. Debes indicar un canal válido:\n" +
                    "🔹 Con texto: `!Configurar [#canal]` (Ej: `!Configurar #anuncios`)\n" +
                    "🔹 Con barra: `/configuracion canal: [#canal]`");
        } else {
            // Guardamos en la base de datos usando el ID del servidor y del canal desde el contexto 🌟
            ConexionBD.getConexionBD().guardarCanalAlertas(ctx.getGuildId(), canalMencionado.getId());

            ctx.responder("✅ **Configuración guardada**. A partir de ahora, todas las alarmas automáticas se enviarán a " + canalMencionado.getAsMention());
        }
    }
}