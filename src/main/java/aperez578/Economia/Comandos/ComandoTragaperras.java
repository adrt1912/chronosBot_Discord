package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Random;

public class ComandoTragaperras implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        String userId= ctx.getIdAutor();
        long cantidad = ctx.getParametroLong("cantidad");
        long[] datosC= EconomiaBD.obtenerPerfilEconomia(userId);
        if(cantidad<=0) ctx.responder("❌ **Error de Apuesta** | La cantidad a arriesgar debe ser mayor que 0. ¡No intentes engañar a la banca!");
        else if(datosC[0]<cantidad) ctx.responder("💸 **Bancarrota** | No tienes suficientes monedas en tu cartera para realizar esta apuesta. ¡Ve a `/trabajar` primero!");
        else{
            String[] emojis = {"🍎", "🍌", "🍒", "🍉", "💎"};

            Random random=new Random();
            int pos1= random.nextInt(0,5);
            String e1=emojis[pos1];

            int pos2= random.nextInt(0,5);
            String e2=emojis[pos2];

            int pos3= random.nextInt(0,5);
            String e3=emojis[pos3];

            String rodillos = "🎰 **MÁQUINA TRAGAPERRAS** 🎰\n" +
                    " ╔═════════╗\n" +
                    " ║  " + e1 + " | " + e2 + " | " + e3 + "  ║\n" +
                    " ╚═════════╝\n\n";
            if(pos1==pos2&& pos1==pos3){

                EconomiaBD.actualizarEconomia(userId,datosC[0]+cantidad*3,datosC[1]);
                ctx.responder(rodillos + "🎉 ✨ **¡¡JACKPOT ENORME!!** ✨ 🎉\n" +
                        " Han coincidido los tres rodillos. Te llevas un premio gordo de **" + cantidad*3 + "** monedas. 💰");
            }else if(pos1==pos2||pos1==pos3||pos3==pos2){
                EconomiaBD.actualizarEconomia(userId,datosC[0]+cantidad,datosC[1]);
                ctx.responder(rodillos + "✨ **¡Premio Parcial!** ✨\n" +
                        "¡Dos rodillos han coincidido! Mantienes la racha y te embolsas **" + cantidad + "** monedas extra.");
            }else {
                EconomiaBD.actualizarEconomia(userId,datosC[0]-cantidad,datosC[1]);
                ctx.responder(rodillos + "❌ **Mala Suerte** ❌\n" +
                        "Ningún rodillo coincide en esta tirada. Has perdido **" + cantidad + "** monedas. ¡La banca gana!");
            }
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return  Commands.slash("tragaperras", "Prueba tu suerte en la máquina tragaperras de Chronos.")
                .addOption(OptionType.INTEGER, "cantidad", "La cantidad de monedas que quieres apostar.", true);
    }
}
