package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Random;

public class ComandoRobar implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        String idusuario=ctx.getIdAutor();
        long[] datosUsuario= EconomiaBD.obtenerPerfilEconomia(idusuario);
        String idVictima=ctx.getParametroString("usuario");
        User victima = ctx.getParametroUser("usuario");
        long[] datosVicti=EconomiaBD.obtenerPerfilEconomia(idVictima);
        if (idVictima.equals(idusuario)) ctx.responder("¿Qué haces intentando robarte a ti mismo? ¡Quiérete un poco más! 😭");
        else if (victima.isBot()) ctx.responder("🤖 **Operación Cancelada** | ¿Intentas robarle a un bot? Tienen cables, no billeteras.");
        else if (datosUsuario[0] < 100) ctx.responder("💸 No tienes ni 100 monedas para pagar una posible multa. ¡Ponte a `/trabajar` antes de delinquir!");
        else if (datosVicti[0] <= 150) ctx.responder("Bro, esa persona está en la miseria, déjala tranquila... 🥺");
        else {
            Random random = new Random();
            int probRob= random.nextInt(0,101);
            if(probRob<40){
                int multa=random.nextInt(0,100);
                EconomiaBD.actualizarEconomia(idusuario,datosUsuario[0]-multa,datosUsuario[1]);
                EconomiaBD.actualizarEconomia(idVictima, datosVicti[0] + multa, datosVicti[1]);
                ctx.responder("🚨 👮‍♂️ **¡ALTO A LA POLICÍA!** 👮‍♂️ 🚨\nHas hecho demasiado ruido intentando robar a **" + victima.getEffectiveName() + "**. Te han pillado con las manos en la masa. Se te ha aplicado una multa de **" + multa + "** monedas que han sido transferidas a su cuenta como compensación. 🚓");
            }else{
                int cantRob=random.nextInt(10,151);
                EconomiaBD.actualizarEconomia(idusuario,datosUsuario[0]+cantRob,datosUsuario[1]);
                EconomiaBD.actualizarEconomia(idVictima,datosVicti[0]-cantRob,datosVicti[1]);
                ctx.responder("🥷 ✨ **¡EL GOLPE PERFECTO!** ✨\nTe has deslizado entre las sombras y le has mangado **" + cantRob + "** monedas de la cartera a **" + victima.getEffectiveName() + "** sin que se dé cuenta. ¡A casa con el botín! 💰");
            }
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("robar", "Intenta robarle algunas monedas a otro usuario, ¡pero cuidado con la multa si te pillan!")
                .addOption(OptionType.USER, "usuario", "El usuario al que intentas robar.", true);
    }
}
