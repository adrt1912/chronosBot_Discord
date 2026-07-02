package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ComandoBorrar implements Comando {


    public void ejecutar(MessageReceivedEvent event) {
        String[] mensaje = event.getMessage().getContentRaw().split(" ");
        if (mensaje.length < 2) event.getChannel().sendMessage("⚠️ **Uso incorrecto** añada un parametro").queue();
        else {
                // 2. Protegemos la conversión de texto a número
                try {
                    int idTarea = Integer.parseInt(mensaje[1]); // Aquí es donde antes fallaba con "sag"
                    boolean exito = ConexionBD.getConexionBD().borrarTarea(idTarea);

                    if (exito) event.getChannel().sendMessage("✅ Tarea eliminada correctamente.").queue();
                    else event.getChannel().sendMessage("❌ Error: No se encontró ninguna tarea con ese ID.").queue();

                } catch (NumberFormatException e) {
                    // 🛡️ El escudo: Si ponen "sag", "hola" o letras, entra aquí y responde amablemente
                    event.getChannel().sendMessage("❌ El parámetro debe ser un número entero válido. Ejemplo: `!EliminarTarea 5`").queue();
                }
            }

    }
}
