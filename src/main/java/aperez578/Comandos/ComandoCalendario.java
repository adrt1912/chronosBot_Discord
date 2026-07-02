package aperez578.Comandos;

import aperez578.BotonesEventos;
import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.Tarea;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction; // 🌟 NUEVO IMPORT

import java.awt.Color;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ComandoCalendario implements Comando {

    @Override
    public void ejecutar(MessageReceivedEvent event) {
        // 1. Buscamos y ordenamos las tareas del canal
        List<Tarea> tareaList = ConexionBD.getConexionBD().listarTareasServer(event.getChannel().getId());
        tareaList = tareaList.stream()
                .sorted(Comparator.comparing(Tarea::getFecha).thenComparing(Tarea::getHora))
                .toList();
        String[] mensajedividido = event.getMessage().getContentRaw().split(" ");

        if (mensajedividido.length > 1) {
            String tipoBuscar = mensajedividido[1];
            String fraseCompleta = String.join(" ", java.util.Arrays.copyOfRange(mensajedividido, 1, mensajedividido.length)).toLowerCase();
            switch (tipoBuscar) {
                case "hoy", "Hoy" ->
                        tareaList = tareaList.stream().filter(tarea -> Objects.equals(tarea.getFecha(), LocalDate.now())).toList();
                case "Mañana", "mañana", "manana" ->
                        tareaList = tareaList.stream().filter(tarea -> Objects.equals(tarea.getFecha(), LocalDate.now().plusDays(1))).toList();
                case "semana", "Semana" -> {
                    LocalDate hoy = LocalDate.now();
                    LocalDate limite = hoy.plusDays(7); // Margen de 7 días
                    tareaList = tareaList.stream()
                            .filter(tarea -> !tarea.getFecha().isBefore(hoy) && !tarea.getFecha().isAfter(limite)).toList();
                }
                case "Mes", "mes", "Mensual", "mensual" -> {
                    LocalDate hoy = LocalDate.now();
                    LocalDate limite = hoy.plusMonths(1);
                    tareaList = tareaList.stream()
                            .filter(tarea -> !tarea.getFecha().isBefore(hoy) && !tarea.getFecha().isAfter(limite)).toList();
                }
                default -> tareaList = tareaList.stream().filter(tarea -> tarea.getTitulo().toLowerCase().contains(fraseCompleta.toLowerCase())).toList();
            }
        }
        if (tareaList.isEmpty()) {
            EmbedBuilder embedVacio = new EmbedBuilder()
                    .setTitle("📅 Calendario Vacío")
                    .setColor(Color.GRAY)
                    .setDescription("No hay eventos programados que coincidan con tu búsqueda en este canal.");

            event.getChannel().sendMessageEmbeds(embedVacio.build()).queue();
        }else {
            // Enviamos un mensaje de cabecera para abrir el calendario
            event.getChannel().sendMessage("🗓️ **__CALENDARIO DE EVENTOS PRÓXIMOS__** 🗓️\nAquí tienes las citas programadas para este canal:").queue();

            // 3. Recorremos las tareas mandando una tarjeta independiente para cada una
            for (Tarea tarea : tareaList) {
                EmbedBuilder embedTarea = new EmbedBuilder();
                embedTarea.setTitle("📌 " + tarea.getTitulo())
                        .setColor(new Color(0x3498db)) // Azul elegante
                        .addField("🆔 ID del Evento:", "`" + tarea.getId() + "`", true)
                        .addField("👤 Organiza:", "<@" + tarea.getUserID() + ">", true)
                        .addField("⏰ Fecha y Hora:", "`" + tarea.getFecha() + "` a las `" + tarea.getHora() + "` hs", false)
                        .setFooter("Chronos Bot", event.getJDA().getSelfUser().getAvatarUrl());

                MessageCreateAction accion = event.getChannel().sendMessageEmbeds(embedTarea.build());
                BotonesEventos.getBotonesEventos().aplicarBotones(accion, tarea);
                accion.queue();
            }
        }
        }
    }
