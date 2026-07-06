package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Random;

public class ComandoRuleta implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {

        String userId= ctx.getIdAutor();
        long cantidad = ctx.getParametroLong("cantidad");
        String opcion = ctx.getParametroString("color");
        long[] datosC= EconomiaBD.obtenerPerfilEconomia(userId);
        String ganadorColor;
        if(cantidad<=0) ctx.responder("❌ **Error de Apuesta** | La cantidad a arriesgar debe ser mayor que 0. ¡No intentes engañar a la banca!");
        else if(datosC[0]<cantidad) ctx.responder("💸 **Bancarrota** | No tienes suficientes monedas en tu cartera para realizar esta apuesta. ¡Ve a `/trabajar` primero!");
        else{

            Random random=new Random();
            int numA= random.nextInt(0,1001);
            if(numA<495) ganadorColor="rojo";
            else if(numA<990) ganadorColor="negro";
            else ganadorColor="verde";

            long cantidadGanada=datosC[0];
            if(opcion.equals(ganadorColor)&&!opcion.equals("verde")){
                cantidadGanada = cantidadGanada + cantidad;
                ctx.responder("🎉 **¡La ruleta se detiene!**\nHa salido el color **" + ganadorColor.toUpperCase() + "** y has acertado. ¡Has duplicado tu apuesta ganando **" + cantidad + "** monedas! 💰");
            }else if(opcion.equals(ganadorColor)){
                cantidadGanada=cantidadGanada+cantidad* 35L;
                ctx.responder("🟢 ✨ **¡¡EL PREMIO GORDO DEL CASINO!!** ✨ 🟢\n¡La bola ha caído milagrosamente en el **VERDE**! Has multiplicado tu apuesta por 35. ¡Te llevas **" + (cantidad * 35) + "** monedas de golpe de la banca! 🚀");
            }else{
                cantidadGanada=cantidadGanada-cantidad;
                String emojiGanador = ganadorColor.equals("rojo") ? "🔴" : (ganadorColor.equals("negro") ? "⚫" : "🟢");
                ctx.responder("💀 **La banca gana** | La bola cayó en el " + emojiGanador + " **" + ganadorColor.toUpperCase() + "**. Has perdido tus **" + cantidad + "** monedas apostadas... ¡La suerte cambia en la próxima ronda!");
            }
            EconomiaBD.actualizarEconomia(userId, cantidadGanada, datosC[1]);
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return                            Commands.slash("ruleta", "Prueba tu suerte en la ruleta del casino apostando tus monedas.")
                .addOption(OptionType.INTEGER, "cantidad", "La cantidad de monedas que quieres arriesgar.", true)
                .addOptions(new net.dv8tion.jda.api.interactions.commands.build.OptionData(OptionType.STRING, "color", "El color al que quieres apostar.", true)
                        .addChoice("Rojo 🔴", "rojo")
                        .addChoice("Negro ⚫", "negro")
                        .addChoice("Verde 🟢", "verde")
                );
    }
}
