package aperez578.Notificaciones.Comandos;

import aperez578.*;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class ComandoCerrarEncuesta implements Comando {

    @Override // 🌟 Añadimos la anotación obligatoria de la interfaz
    public void ejecutar(ContextoComando ctx) {
        try {
            int idComand = ctx.getParametroInt("id");

            Tarea tareaCambiar = NotificacionesBD.obtenerTareaPorId(idComand);

            if (tareaCambiar == null) {
                ctx.responder("⚠️ **Error:** No existe ningún evento o encuesta activa con el ID `" + idComand + "`.");
                return;
            }

            //  CASO 1: EVENTO COMUNITARIO (Botones de Asistencia)
            if (tareaCambiar.getBotonesTipo() == 1) {
                List<String> list = NotificacionesBD.obtenerAsistentes(idComand);
                long numAsistentes = list.size();

                GestorLogs.enviarLog(ctx, "Calendario: Evento Finalizado y Cerrado",
                        "🏁 El evento **" + tareaCambiar.getTitulo() + "** (ID: `" + idComand + "`) ha concluido.");

                String texto = "🏁 **¡Evento Finalizado!** 🏁\n" +
                        "El evento **" + tareaCambiar.getTitulo() + "** ha cerrado sus puertas.\n" +
                        "👥 **Asistentes totales:** " + numAsistentes + " personas.";

                ctx.responder(texto);
                NotificacionesBD.borrarTarea(idComand);
            }

            // 📊 CASO 2: ENCUESTA DE OPCIONES
            else if (tareaCambiar.getBotonesTipo() == 2) {
                String[] opciones = tareaCambiar.getOpciones().split("\\|");
                List<String> list = NotificacionesBD.obtenerTodosLosVotos(idComand);
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
                NotificacionesBD.borrarTarea(idComand);
            }
            //  CASO 3: SIN BOTONES (Solo aviso previo)
            else {
                ctx.responder("🗑️ El aviso del evento **" + tareaCambiar.getTitulo() + "** ha sido cerrado y retirado.");
                NotificacionesBD.borrarTarea(idComand);
            }

        } catch (Exception e) {
            // Atrapa si ponen letras o si ejecutan el comando vacío
            ctx.responder("""
                    ❌ **Error:** Debes proporcionar un ID de evento numérico válido.
                    🔹 Con prefijo: `!Cerrar [ID]` (Ej: `!Cerrar 12`)
                    🔹 Con barra: `/cerrar id: [ID]`""");
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return   Commands.slash("cerrar", "Cierra la encuesta de un evento, así no se registran más resultados")
                .addOptions(new OptionData(OptionType.INTEGER, "id", "El ID numérico del evento", true).setAutoComplete(true));
    }
}