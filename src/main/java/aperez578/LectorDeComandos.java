package aperez578;

import aperez578.Comandos.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectorDeComandos extends ListenerAdapter {

    private final Map<String, Comando> comandos = new HashMap<>();

    public LectorDeComandos() {
        comandos.put("!Ping", new ComandoPing());
        comandos.put("!CrearNotificacion", new ComandoCrearNotificacion());
        comandos.put("!ListarNotificacion", new ComandoListar());
        comandos.put("!Ayuda", new ComandoAyuda());
        comandos.put("!EliminarTarea", new ComandoBorrar());
        comandos.put("!Calendario", new ComandoCalendario());
        comandos.put("!EditarNotificacion", new ComandoEditarNotificacion());
        comandos.put("!Configuracion",new ComandoConfigurar());
        comandos.put("!Cerrar",new ComandoCerrarEncuesta());
        comandos.put("!MisEventos",new ComandoMisEventos());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String[] textoEntero = event.getMessage().getContentRaw().split(" ");
        String comandoEscrito = textoEntero[0];

        Comando comando = comandos.get(comandoEscrito);
        if (comando != null) comando.ejecutar(event);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String idBoton = event.getComponentId();
        String[] partest = idBoton.split("_");

        int id = Integer.parseInt(partest[partest.length - 1]);

        if (ConexionBD.getConexionBD().obtenerTareaPorId(id) == null) {
            event.reply("❌ Lo siento, este evento o encuesta ya ha finalizado.")
                    .setEphemeral(true) // Hace que el mensaje solo lo vea el que pulsó
                    .queue();
        } else {
            String userid = event.getUser().getId();
            String buttonid = event.getComponentId();
            ConexionBD bd = ConexionBD.getConexionBD();
            int idTarea = -1;

            // 📅 CASO 1: PULSAN "ASISTIR"
            if (buttonid.startsWith("asistir_")) {
                idTarea = Integer.parseInt(buttonid.replace("asistir_", ""));
                bd.apuntarseTarea(idTarea, userid, "SI");
                // Usamos deferEdit() para decirle a Discord que procesamos el clic de forma silenciosa
                event.deferEdit().queue();
            }
            // 🔄 CASO 2: PULSAN "NO ASISTIR"
            else if (buttonid.startsWith("desapuntarse_")) {
                idTarea = Integer.parseInt(buttonid.replace("desapuntarse_", ""));
                bd.desApuntareseEvento(idTarea, userid);
                event.deferEdit().queue();
            }
            // 📊 CASO 3: PULSAN UNA OPCIÓN DE LA ENCUESTA
            else if (buttonid.startsWith("voto_")) {
                String[] partes = buttonid.split("_");
                int indiceVoto = Integer.parseInt(partes[1]);
                idTarea = Integer.parseInt(partes[2]);

                bd.apuntarseTarea(idTarea, userid, String.valueOf(indiceVoto));
                event.deferEdit().queue();
            }

            // 🌟 SI SE DETECTÓ UNA TAREA VÁLIDA, REDIBUJAMOS LA TARJETA EN VIVO
            if (idTarea != -1) {
                actualizarTarjetaEnVivo(event, idTarea);
            }
        }
    }

        // 🛠️ MÉTODOD AUXILIAR PARA REDIBUJAR LA TARJETA
        private void actualizarTarjetaEnVivo (ButtonInteractionEvent event,int idTarea){
            ConexionBD bd = ConexionBD.getConexionBD();
            Tarea tarea = bd.obtenerTareaPorId(idTarea);

            if (tarea == null) return; // Si la tarea ya no existe, cancelamos

            // Reconstruimos la estructura base del Embed original
            net.dv8tion.jda.api.EmbedBuilder nuevoEmbed = new net.dv8tion.jda.api.EmbedBuilder()
                    .setTitle("📌 " + tarea.getTitulo())
                    .addField("🆔 ID del Evento:", "`" + tarea.getId() + "`", true)
                    .addField("👤 Organiza:", "<@" + tarea.getUserID() + ">", true)
                    .addField("⏰ Fecha y Hora:", "`" + tarea.getFecha() + "` a las `" + tarea.getHora() + "` hs", false)
                    .setFooter("Chronos Bot • Actualizado en vivo", event.getJDA().getSelfUser().getAvatarUrl())
                    .setTimestamp(java.time.Instant.now());

            // 🟢 SI ES UN EVENTO COMUNITARIO (Tipo 1): Mostramos la lista de nombres
            if (tarea.getBotonesTipo() == 1) {
                nuevoEmbed.setColor(java.awt.Color.GREEN);
                List<String> asistentes = bd.obtenerAsistentes(idTarea);

                if (asistentes.isEmpty())
                    nuevoEmbed.addField("👥 Asistentes (0):", "*Nadie se ha apuntado todavía...*", false);
                else {
                    StringBuilder sb = new StringBuilder();
                    for (String userId : asistentes) {
                        sb.append("<@").append(userId).append("> "); // Los mencionamos dinámicamente
                    }
                    nuevoEmbed.addField("👥 Asistentes (" + asistentes.size() + "):", sb.toString(), false);
                }
            } else if (tarea.getBotonesTipo() == 2) {
                nuevoEmbed.setColor(new java.awt.Color(0x3498db));
                String[] opciones = tarea.getOpciones().split("\\|");
                List<String> todosLosVotos = bd.obtenerTodosLosVotos(idTarea);

                nuevoEmbed.setDescription("📊 **Resultados de la encuesta actualizados:**");

                for (int i = 0; i < opciones.length; i++) {
                    String nombreOpcion = opciones[i].trim();
                    final String indiceString = String.valueOf(i);

                    // Contamos cuántas veces aparece este índice en la lista de votos
                    long numVotos = todosLosVotos.stream().filter(v -> v.equals(indiceString)).count();

                    nuevoEmbed.addField(nombreOpcion, "🔹 `" + numVotos + "` votos", true);
                }
            }
            event.getHook().editOriginalEmbeds(nuevoEmbed.build()).queue();
        }

}