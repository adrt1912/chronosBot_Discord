package Aperez578.Economia.Comandos;

import Aperez578.Comando;
import Aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Random;

public class ComandoDados implements Comando {

    private final Random random=new Random();

    @Override
    public void ejecutar(ContextoComando ctx) {
        String userId= ctx.getIdAutor();
        long cantidad = ctx.getParametroLong("cantidad");
        long[] datosC= EconomiaBD.obtenerPerfilEconomia(userId);
        if(cantidad<=0) ctx.responder("❌ **Error de Apuesta** | La cantidad a arriesgar debe ser mayor que 0. ¡No intentes engañar a la banca!");
        else if(datosC[0]<cantidad) ctx.responder("💸 **Bancarrota** | No tienes suficientes monedas en tu cartera para realizar esta apuesta. ¡Ve a `/trabajar` primero!");

        else{
            int dado1Jugador= random.nextInt(1,7);
            int dado2Jugador=random.nextInt(1,7);
            int totalJugador=dado1Jugador+dado2Jugador;

            int dado1Juego=random.nextInt(1,7);
            int dado2Juego=random.nextInt(1,7);
            int totalJuego=dado1Juego+dado2Juego;

            String resultadoDados = "🎲 **Tus dados:** " + dado1Jugador + " + " + dado2Jugador + " (Total: **" + totalJugador + "**)\n" +
                    "🤖 **Dados de Chronos:** " + dado1Juego + " + " + dado2Juego + " (Total: **" + totalJuego + "**)\n\n";

            if(totalJugador>totalJuego){
                EconomiaBD.actualizarEconomia(userId,datosC[0]+cantidad,datosC[1]);
                ctx.responder(resultadoDados + "🎉 ¡**Has ganado**! Te llevas **" + cantidad + "** monedas a casa.");
            }else if(totalJugador<totalJuego){
                EconomiaBD.actualizarEconomia(userId,datosC[0]-cantidad,datosC[1]);
                ctx.responder(resultadoDados + "📉 ¡**Has perdido**! Chronos se queda con tus **" + cantidad + "** monedas.");
            }
            else ctx.responder(resultadoDados + "🤝 **¡Empate!** Las puntuaciones son iguales. Recuperas tus monedas intactas.");
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return  Commands.slash("dados", "Apuesta tus monedas en una partida de dados contra Chronos.")
                .addOption(OptionType.INTEGER, "cantidad", "La cantidad de monedas que quieres apostar.", true);
    }
}