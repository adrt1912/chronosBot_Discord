package aperez578.Notificaciones.Comandos;

import aperez578.*;

public class ComandoBorrar implements Comando {

    @Override // 🌟 Añadimos la anotación de la interfaz
    public void ejecutar(ContextoComando ctx) {
        try {
            int idTarea = ctx.getParametroInt("id");
            Tarea tarea=ConexionBD.getConexionBD().obtenerTareaPorId(idTarea);
            // Ejecutamos el borrado en la Base de Datos
            boolean exito = ConexionBD.getConexionBD().borrarTarea(idTarea);

            if (tarea != null) {
                GestorLogs.enviarLog(ctx, "Evento Eliminado Manualmente",
                        "📌 **Título:** " + tarea.getTitulo() + "\n" +
                                "🆔 **ID del evento:** `" + idTarea + "`\n" +
                                "👤 **Organizador original:** <@" + tarea.getUserID() + ">");
            }
             else ctx.responder("❌ Error: No se encontró ninguna tarea con ese ID.");

        } catch (Exception e) {
            ctx.responder("❌ **Error:** El parámetro debe ser un número entero válido y obligatorio.\n" +
                    "🔹 Uso con prefijo: `!EliminarTarea 5`\n" +
                    "🔹 Uso con barra: `/eliminar-tarea id: 5`");
        }
    }
}