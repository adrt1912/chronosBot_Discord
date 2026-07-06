package aperez578.Notificaciones.Comandos;

import aperez578.*;

import java.util.List;

public class ComandoCerrarEncuesta implements Comando {

    @Override // 🌟 Añadimos la anotación obligatoria de la interfaz
    public void ejecutar(ContextoComando ctx) {
        try {
            int idComand = ctx.getParametroInt("id");

            Tarea tareaCambiar = ConexionBD.getConexionBD().obtenerTareaPorId(idComand);

            if (tareaCambiar == null) {
                ctx.responder("⚠️ **Error:** No existe ningún evento o encuesta activa con el ID `" + idComand + "`.");
                return;
            }

            // 📅 CASO 1: EVENTO COMUNITARIO (Botones de Asistencia)
            if (tareaCambiar.getBotonesTipo() == 1) {
                List<String> list = ConexionBD.getConexionBD().obtenerAsistentes(idComand);
                long numAsistentes = list.size();

                GestorLogs.enviarLog(ctx, "Calendario: Evento Finalizado y Cerrado",
                        "🏁 El evento **" + tareaCambiar.getTitulo() + "** (ID: `" + idComand + "`) ha concluido.");

                String texto = "🏁 **¡Evento Finalizado!** 🏁\n" +
                        "El evento **" + tareaCambiar.getTitulo() + "** ha cerrado sus puertas.\n" +
                        "👥 **Asistentes totales:** " + numAsistentes + " personas.";

                ctx.responder(texto);
                ConexionBD.getConexionBD().borrarTarea(idComand);
            }

            // 📊 CASO 2: ENCUESTA DE OPCIONES
            else if (tareaCambiar.getBotonesTipo() == 2) {
                String[] opciones = tareaCambiar.getOpciones().split("\\|");
                List<String> list = ConexionBD.getConexionBD().obtenerTodosLosVotos(idComand);
                int[] contadores = new int[opciones.length];

                for (String voto : list) {
                    try {
                        int indiceVoto = Integer.parseInt(voto);
                        if (indiceVoto >= 0 && indiceVoto < contadores.length) contadores[indiceVoto]++;
                    } catch (NumberFormatException e) {
                        // Ignoramos votos corruptos limpiamente
                    }
                }

                StringBuilder sb = new StringBuilder();
                sb.append("📊 **¡Encuesta Cerrada!** 📊\n");

                sb.append("Resultados finales para **").append(tareaCambiar.getTitulo()).append("**:\n\n");

                for (int i = 0; i < opciones.length; i++) {
                    sb.append("🔹 **").append(opciones[i].trim()).append("**: ").append(contadores[i]).append(" votos\n");
                }
                GestorLogs.enviarLog(ctx, "Encuesta: Votación Cerrada",
                        "📊 Se han clausurado las urnas para **" + tareaCambiar.getTitulo() + "** (ID: `" + idComand + "`).");

                ctx.responder(sb.toString());
                ConexionBD.getConexionBD().borrarTarea(idComand);
            }
            // ❌ CASO 3: SIN BOTONES (Solo aviso previo)
            else {
                ctx.responder("🗑️ El aviso del evento **" + tareaCambiar.getTitulo() + "** ha sido cerrado y retirado.");
                ConexionBD.getConexionBD().borrarTarea(idComand);
            }

        } catch (Exception e) {
            // 🛡️ El escudo: Atrapa si ponen letras o si ejecutan el comando vacío
            ctx.responder("❌ **Error:** Debes proporcionar un ID de evento numérico válido.\n" +
                    "🔹 Con prefijo: `!Cerrar [ID]` (Ej: `!Cerrar 12`)\n" +
                    "🔹 Con barra: `/cerrar id: [ID]`");
        }
    }
}