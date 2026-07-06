package aperez578.Notificaciones.Comandos;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter; // 🌟 IMPORTANTE: El motor de eventos de JDA
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

import java.util.ArrayList;
import java.util.List;

public class BotonesEventos extends ListenerAdapter {

    private static BotonesEventos instancia;

    // Constructor privado limpio
    private BotonesEventos() {}

    public static BotonesEventos getBotonesEventos() {
        if (instancia == null) instancia = new BotonesEventos();
        return instancia;
    }

    public void aplicarBotones(MessageCreateAction accion, Tarea tarea) {
        switch (tarea.getBotonesTipo()) {
            case 1: // 📅 BOTONES DE ASISTENCIA
                Button botonSi = Button.success("asistir_" + tarea.getId(), "✅ Asistiré");
                Button botonNo = Button.danger("desapuntarse_" + tarea.getId(), "❌ No asistiré");
                accion.addActionRow(botonSi, botonNo);
                break;
            case 2: // 📊 ENCUESTA CON OPCIONES RELLENABLES
                String opcionesRaw = tarea.getOpciones();
                if (opcionesRaw == null || opcionesRaw.isEmpty()) opcionesRaw = "A|B|C|D";

                String[] opciones = opcionesRaw.split("\\|");
                List<Button> listaBotones = new ArrayList<>();

                for (int i = 0; i < opciones.length; i++) {
                    String nombreOpcion = opciones[i].trim();
                    String idSecreto = "voto_" + i + "_" + tarea.getId();
                    listaBotones.add(Button.primary(idSecreto, nombreOpcion));
                }
                accion.setActionRow(listaBotones);
                break;
            default:
                // Tipo 0: Sin botones
        }
    }

    @Override // ¡Ahora IntelliJ ya sabe qué estás sobrescribiendo y no dará error!
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();

        if (buttonId.startsWith("cal:")) {
            String[] trozos = buttonId.split(":", 4);
            String accion = trozos[1];
            int paginaActual = Integer.parseInt(trozos[2]);
            String filtro = trozos[3];

            int nuevaPagina = accion.equals("ant") ? paginaActual - 1 : paginaActual + 1;

            List<Tarea> tareas = ComandoCalendario.obtenerTareasFiltradas(event.getChannel().getId(), filtro);

            if (tareas.isEmpty() || nuevaPagina >= tareas.size() || nuevaPagina < 0) {
                event.editMessage("⚠️ Este calendario ya no está sincronizado. Por favor, usa `/calendario` de nuevo.").setComponents().setEmbeds().queue();
                return;
            }

            Tarea tarea = tareas.get(nuevaPagina);
            int total = tareas.size();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("📌 " + tarea.getTitulo())
                    .setColor(new java.awt.Color(0x3498db))
                    .addField("🆔 ID del Evento:", "`" + tarea.getId() + "`", true)
                    .addField("👤 Organiza:", "<@" + tarea.getUserID() + ">", true)
                    .addField("⏰ Fecha y Hora (Tu hora local):", "<t:" + tarea.getTimestamp() + ":F> (<t:" + tarea.getTimestamp() + ":R>)", false)
                    .setFooter("Evento " + (nuevaPagina + 1) + " de " + total + " • Chronos Bot", event.getJDA().getSelfUser().getAvatarUrl());

            List<ActionRow> filas = new ArrayList<>();

            List<Button> botonesOperativos = new ArrayList<>();
            if (tarea.getBotonesTipo() == 1) {
                botonesOperativos.add(Button.success("asistir_" + tarea.getId(), "📅 Asistir"));
                botonesOperativos.add(Button.danger("desapuntarse_" + tarea.getId(), "❌ No Asistir"));
            } else if (tarea.getBotonesTipo() == 2 && tarea.getOpciones() != null) {
                String[] ops = tarea.getOpciones().split("\\|");
                for (int i = 0; i < ops.length; i++) {
                    botonesOperativos.add(Button.primary("voto_" + i + "_" + tarea.getId(), ops[i].trim()));
                }
            }
            if (!botonesOperativos.isEmpty()) filas.add(ActionRow.of(botonesOperativos));

            Button btnAnt = Button.secondary("cal:ant:" + nuevaPagina + ":" + filtro, "◀️ Anterior");
            if (nuevaPagina == 0) btnAnt = btnAnt.asDisabled();

            Button btnSig = Button.secondary("cal:sig:" + nuevaPagina + ":" + filtro, "Siguiente ▶️");
            if (nuevaPagina == total - 1) btnSig = btnSig.asDisabled();

            filas.add(ActionRow.of(btnAnt, btnSig));

            event.editMessageEmbeds(embed.build()).setComponents(filas).queue();
        }
    }
}