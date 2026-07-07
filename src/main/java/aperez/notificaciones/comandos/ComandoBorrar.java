package aperez.notificaciones.comandos;

import aperez.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ComandoBorrar implements Comando {

    @Override // 🌟 Añadimos la anotación de la interfaz
    public void ejecutar(ContextoComando ctx) {
        try {
            int idTarea = ctx.getParametroInt("id");
            Tarea tarea=NotificacionesBD.obtenerTareaPorId(idTarea);
            // Ejecutamos el borrado en la Base de Datos

            if (tarea != null) {
                GestorLogs.enviarLog(ctx, "Evento Eliminado Manualmente",
                        "📌 **Título:** " + tarea.titulo() + "\n" +
                                "🆔 **ID del evento:** `" + idTarea + "`\n" +
                                "👤 **Organizador original:** <@" + tarea.userID() + ">");
            }
             else ctx.responder("❌ Error: No se encontró ninguna tarea con ese ID.");

        } catch (Exception _) {
            ctx.responder("""
                    ❌ **Error:** El parámetro debe ser un número entero válido y obligatorio.
                    🔹 Uso con prefijo: `!EliminarTarea 5`
                    🔹 Uso con barra: `/eliminar-tarea id: 5`""");
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("eliminar-tarea", "Elimina una tarea según su ID")
                .addOptions(new OptionData(OptionType.INTEGER, "id", "El ID numérico del evento", true).setAutoComplete(true));
    }
}