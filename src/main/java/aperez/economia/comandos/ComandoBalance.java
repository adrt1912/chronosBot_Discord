package aperez.economia.comandos;

import aperez.Comando;
import aperez.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ComandoBalance implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        String userID= ctx.getIdAutor();
        long[] datos= EconomiaBD.obtenerPerfilEconomia(userID);
        ctx.responder("💰 **Banco de Chronos** | Tienes un total de **" + datos[0] + "** monedas resguardadas en tu cuenta.");    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("balance","Consulta cuántas monedas tienes ahorradas en tu cuenta bancaria.");
    }
}