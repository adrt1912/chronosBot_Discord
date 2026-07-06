package aperez578;

import aperez578.Economia.Comandos.*;
import aperez578.Experiencia.Comandos.ComandoRank;
import aperez578.Experiencia.Comandos.ComandoTopXP;
import aperez578.Notificaciones.Comandos.*;
import aperez578.Utilidad.Comandos.ComandoAyuda;
import aperez578.Utilidad.Comandos.ComandoClean;
import aperez578.Utilidad.Comandos.ComandoWarn;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LectorDeComandos extends ListenerAdapter {

    private final Map<String, Comando> comandos = new HashMap<>();

    public LectorDeComandos() {
        comandos.put("ping", new ComandoPing());
        comandos.put("crear-notificacion", new ComandoCrearNotificacion());
        comandos.put("listar-notificaciones", new ComandoListar());
        comandos.put("ayuda", new ComandoAyuda(this));
        comandos.put("eliminar-tarea", new ComandoBorrar());
        comandos.put("calendario", new ComandoCalendario());
        comandos.put("editar-notificacion", new ComandoEditarNotificacion());
        comandos.put("configuracion",new ComandoConfigurar());
        comandos.put("cerrar",new ComandoCerrarEncuesta());
        comandos.put("mis-eventos",new ComandoMisEventos());
        comandos.put("listar-resultados",new ComandoListarResultados());
        comandos.put("recordatorio",new ComandoRecordatorio());
        comandos.put("trabajar",new ComandoTrabajar());
        comandos.put("balance",new ComandoBalance());
        comandos.put("ruleta",new ComandoRuleta());
        comandos.put("robar",new ComandoRobar());
        comandos.put("top",new ComandoTop());
        comandos.put("transferir",new ComandoTransferir());
        comandos.put("dados",new ComandoDados());
        comandos.put("tragaperras",new ComandoTragaperras());
        comandos.put("tienda",new ComandoTienda());
        comandos.put("comprar",new ComandoComprar());
        comandos.put("clean",new ComandoClean());
        comandos.put("warn",new ComandoWarn());
        comandos.put("rank",new ComandoRank());
        comandos.put("topxp",new ComandoTopXP());
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String idBoton = event.getComponentId();

        //  Si el botón es del calendario, ignorarlo en esta clase (lo gestiona BotonesEventos)
        if (idBoton.startsWith("cal:")) {
            return;
        }

        // A partir de aquí tu lógica de siempre procesará los botones de asistencia y encuestas de forma segura
        String[] partest = idBoton.split("_");
        int id = Integer.parseInt(partest[partest.length - 1]);

        if (NotificacionesBD.obtenerTareaPorId(id) == null) {
            event.reply("❌ Lo siento, este evento o encuesta ya ha finalizado.")
                    .setEphemeral(true)
                    .queue();
        } else {
            String userid = event.getUser().getId();
            String buttonid = event.getComponentId();

            // 📅 CASO 1: PULSAN "ASISTIR"
            if (buttonid.startsWith("asistir_")) {
                NotificacionesBD.apuntarseTarea(id, userid, "SI");
                event.deferEdit().queue();
            }
            // 🔄 CASO 2: PULSAN "NO ASISTIR"
            else if (buttonid.startsWith("desapuntarse_")) {
                NotificacionesBD.desApuntareseEvento(id, userid);
                event.deferEdit().queue();
            }
            // 📊 CASO 3: PULSAN UNA OPCIÓN DE LA ENCUESTA
            else if (buttonid.startsWith("voto_")) {
                String[] partes = buttonid.split("_");
                int indiceVoto = Integer.parseInt(partes[1]);

                NotificacionesBD.apuntarseTarea(id, userid, String.valueOf(indiceVoto));
                event.deferEdit().queue();
            }
            actualizarTarjetaEnVivo(event, id);
        }
    }

    private void actualizarTarjetaEnVivo (ButtonInteractionEvent event,int idTarea) {
        Tarea tarea = NotificacionesBD.obtenerTareaPorId(idTarea);

        if (tarea != null) {
            long ts = tarea.getTimestamp();

            EmbedBuilder nuevoEmbed = new EmbedBuilder()
                    .setTitle("📌 " + tarea.getTitulo())
                    .addField("🆔 ID del Evento:", "`" + tarea.getId() + "`", true)
                    .addField("👤 Organiza:", "<@" + tarea.getUserID() + ">", true)
                    .addField("⏰ Fecha y Hora (Tu hora local):", "<t:" + ts + ":F> (<t:" + ts + ":R>)", false)
                    .setFooter("Chronos Bot • Actualizado en vivo", event.getJDA().getSelfUser().getAvatarUrl())
                    .setTimestamp(Instant.now());

            if (tarea.getBotonesTipo() == 1) {
                nuevoEmbed.setColor(Color.GREEN);
                List<String> asistentes = NotificacionesBD.obtenerAsistentes(idTarea);

                if (asistentes.isEmpty())
                    nuevoEmbed.addField("👥 Asistentes (0):", "*Nadie se ha apuntado todavía...*", false);
                else {
                    StringBuilder sb = new StringBuilder();
                    for (String userId : asistentes) {
                        sb.append("<@").append(userId).append("> ");
                    }
                    nuevoEmbed.addField("👥 Asistentes (" + asistentes.size() + "):", sb.toString(), false);
                }
            } else if (tarea.getBotonesTipo() == 2) {
                nuevoEmbed.setColor(new Color(0x3498db));
                String[] opciones = tarea.getOpciones().split("\\|");
                List<String> todosLosVotos = NotificacionesBD.obtenerTodosLosVotos(idTarea);

                nuevoEmbed.setDescription("📊 **Resultados de la encuesta actualizados:**");

                for (int i = 0; i < opciones.length; i++) {
                    String nombreOpcion = opciones[i].trim();
                    final String indiceString = String.valueOf(i);

                    long numVotos = todosLosVotos.stream().filter(v -> v.equals(indiceString)).count();
                    nuevoEmbed.addField(nombreOpcion, "🔹 `" + numVotos + "` votos", true);
                }
            }
            event.getHook().editOriginalEmbeds(nuevoEmbed.build()).queue();
        }
    }

    public void despacharSlash(String nombreComando, SlashCommandInteractionEvent event) {
        Comando comando = comandos.get(nombreComando.toLowerCase());
        if (comando != null) comando.ejecutar(new ContextoComando(event));
        else event.reply("❌ Comando no implementado en el sistema central.").setEphemeral(true).queue();
    }

    public List<SlashCommandData> getListaComandData() {
        List<SlashCommandData> lista = new java.util.ArrayList<>();
        // Recorremos todos los comandos que has registrado en tu mapa con .put()
        for (Comando cmd : comandos.values()) {
            lista.add(cmd.getDatosComando());
        }
        return lista;
    }
    public Map<String, Comando> getMapComandos() {
        return comandos;
    }
}