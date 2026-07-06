package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.Instant;
import java.util.Random;

public class ComandoTrabajar implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        long tiempoAhora = Instant.now().getEpochSecond();
        String userId=ctx.getIdAutor();
        long[] user=EconomiaBD.obtenerPerfilEconomia(userId);
        long numMonedas=user[0];
        long tiempo=user[1];
        int tiempoDescanso=3600;
        if(tiempoAhora-tiempo<tiempoDescanso){
            long tiempoRestante = tiempoDescanso - (tiempoAhora - tiempo);
            ctx.responder("⏳ ¡Estás cansado! Debes esperar **" + tiempoRestante/60 + " minutos** para volver a trabajar.");
        }else{
            Random random=new Random();
            int beneficioTrabajo= random.nextInt(50,201);
            long nuevasMonedas = numMonedas + beneficioTrabajo;
            boolean exito=EconomiaBD.actualizarEconomia(userId,nuevasMonedas,tiempoAhora);
            if (exito) ctx.responder("💰 Has trabajado duro y has ganado **" + beneficioTrabajo + " monedas**. Ahora tienes un total de **" + nuevasMonedas + " monedas**.");
            else ctx.responder("❌ Hubo un error al procesar tu salario en el banco central.");
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return   Commands.slash("trabajar","Trabaja duro para ganar unas cuantas monedas");
    }
}