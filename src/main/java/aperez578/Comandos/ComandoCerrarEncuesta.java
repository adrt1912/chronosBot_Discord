package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.Tarea;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class ComandoCerrarEncuesta implements Comando {


    public void ejecutar(MessageReceivedEvent event) {
    String[] mensajedividido=event.getMessage().getContentRaw().split(" ");
    if(mensajedividido.length==2){
        try{
            int idComand= Integer.parseInt(mensajedividido[1]);
            Tarea tareaCambiar= ConexionBD.getConexionBD().obtenerTareaPorId(idComand);
            if(tareaCambiar==null) event.getChannel().sendMessage("⚠️ **Uso incorrecto** no existe un evento con el id "+idComand).queue();
            else {
                if (tareaCambiar.getBotonesTipo() == 1) {
                    List<String> list = ConexionBD.getConexionBD().obtenerAsistentes(idComand);

                    long numAsistentes= list.size();
                    String texto = "🏁 **¡Evento Finalizado!** 🏁\n" +
                            "El evento **" + tareaCambiar.getTitulo() + "** ha cerrado sus puertas.\n" +
                            "👥 **Asistentes totales:** " + numAsistentes + " personas.";
                    event.getChannel().sendMessage(texto).queue();
                    ConexionBD.getConexionBD().borrarTarea(idComand);
                }else if(tareaCambiar.getBotonesTipo()==2){

                    String[] opciones=tareaCambiar.getOpciones().split("\\|");
                    List<String> list=ConexionBD.getConexionBD().obtenerTodosLosVotos(idComand);
                    int[] contadores = new int[opciones.length];
                    for (String voto : list) {
                        try {
                            int indiceVoto = Integer.parseInt(voto);
                            if (indiceVoto >= 0 && indiceVoto < contadores.length) contadores[indiceVoto]++;

                        } catch (NumberFormatException e) {
                            // Por si acaso hay algún voto corrupto, lo ignoramos
                        }
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("📊 **¡Encuesta Cerrada!** 📊\n");
                    sb.append("Resultados finales para **").append(tareaCambiar.getTitulo()).append("**:\n\n");

                    for (int i = 0; i < opciones.length; i++) {
                        sb.append("🔹 **").append(opciones[i]).append("**: ").append(contadores[i]).append(" votos\n");
                    }
                    event.getChannel().sendMessage(sb.toString()).queue();
                    ConexionBD.getConexionBD().borrarTarea(idComand);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    }
}