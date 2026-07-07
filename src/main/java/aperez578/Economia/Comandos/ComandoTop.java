package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Map;

public class ComandoTop implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        Map<String,Long> top= EconomiaBD.obtenerTopEconomida();
        if (top.isEmpty()) ctx.responder("💰 **El banco está vacío** | Aún no hay registros de economía en este servidor.");
       else{
            StringBuilder sb = new StringBuilder();
            sb.append("🏆 **RANKING DE LOS MÁS RICOS** 🏆\n");
            sb.append("Aquí están las 10 fortunas más grandes del servidor:\n");
            sb.append("---------------------------------------------------\n\n");

            int puesto = 1;
            for (Map.Entry<String,Long> entrada: top.entrySet() ){
                String userId= entrada.getKey();
                Long monedas=entrada.getValue();
                String emojiPuesto;
                switch (puesto){
                    case 1->emojiPuesto = "🥇";
                    case 2 ->emojiPuesto = "🥈";
                    case 3 ->emojiPuesto = "🥉";
                    default -> emojiPuesto="⭐ `# " + puesto + "`";
                }

                sb.append(emojiPuesto).append("| <@").append(userId).append("> - **").append(monedas).append("** monedas\n");
                puesto++;
            }
            sb.append("\n---------------------------------------------------");

            // 4. Enviamos el ranking completo
            ctx.responder(sb.toString());
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return  Commands.slash("top", "Muestra el top 10 de los usuarios más ricos del servidor.");
    }
}