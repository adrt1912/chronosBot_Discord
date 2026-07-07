package aperez578.notificaciones.comandos;

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
        switch (tarea.botonesTipo()) {
            case 1: // 📅 BOTONES DE ASISTENCIA
                Button botonSi = Button.success("asistir_" + tarea.id(), "✅ Asistiré");
                Button botonNo = Button.danger("desapuntarse_" + tarea.id(), "❌ No asistiré");
                accion.addActionRow(botonSi, botonNo);
                break;
            case 2: // 📊 ENCUESTA CON OPCIONES RELLENABLES
                String opcionesRaw = tarea.opciones();
                if (opcionesRaw == null || opcionesRaw.isEmpty()) opcionesRaw = "A|B|C|D";

                String[] opciones = opcionesRaw.split("\\|");
                List<Button> listaBotones = new ArrayList<>();

                for (int i = 0; i < opciones.length; i++) {
                    String nombreOpcion = opciones[i].trim();
                    String idSecreto = "voto_" + i + "_" + tarea.id();
                    listaBotones.add(Button.primary(idSecreto, nombreOpcion));
                }
                accion.setActionRow(listaBotones);
                break;
            default:
                // Tipo 0: Sin botones
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();

        // Cláusula de guarda para evitar anidamientos innecesarios
        if (!buttonId.startsWith("cal:")) return;

        String[] trozos = buttonId.split(":", 4);
        String accion = trozos[1];
        int paginaActual = Integer.parseInt(trozos[2]);
        String filtro = trozos[3];

        int nuevaPagina = accion.equals("ant") ? paginaActual - 1 : paginaActual + 1;
        List<Tarea> tareas = ComandoCalendario.obtenerTareasFiltradas(event.getChannel().getId(), filtro);

        if (tareas.isEmpty() || nuevaPagina >= tareas.size() || nuevaPagina < 0) {
            event.editMessage("⚠️ Este calendario ya no está sincronizado. Por favor, usa `/calendario` de nuevo.")
                    .setComponents().setEmbeds().queue();
        } else {

            Tarea tarea = tareas.get(nuevaPagina);
            int total = tareas.size();

            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("📌 " + tarea.titulo())
                    .setColor(new java.awt.Color(0x3498db))
                    .addField("🆔 ID del Evento:", "`" + tarea.id() + "`", true)
                    .addField("👤 Organiza:", "<@" + tarea.userID() + ">", true)
                    .addField("⏰ Fecha y Hora (Tu hora local):", "<t:" + tarea.timestamp() + ":F> (<t:" + tarea.timestamp() + ":R>)", false)
                    .setFooter("Evento " + (nuevaPagina + 1) + " de " + total + " • Chronos Bot", event.getJDA().getSelfUser().getAvatarUrl());

            // Construcción de los componentes usando métodos auxiliares
            List<ActionRow> filas = new ArrayList<>();

            List<Button> botonesOperativos = generarBotonesOperativos(tarea);
            if (!botonesOperativos.isEmpty()) filas.add(ActionRow.of(botonesOperativos));


            filas.add(ActionRow.of(
                    crearBotonAnterior(nuevaPagina, filtro),
                    crearBotonSiguiente(nuevaPagina, total, filtro)
            ));

            event.editMessageEmbeds(embed.build()).setComponents(filas).queue();
        }
    }

// ─── MÉTODOS AUXILIARES PARA REDUCIR COMPLEJIDAD ──────────────────────────────────

    private List<Button> generarBotonesOperativos(Tarea tarea) {
        List<Button> botones = new ArrayList<>();
        if (tarea.botonesTipo() == 1) {
            botones.add(Button.success("asistir_" + tarea.id(), "📅 Asistir"));
            botones.add(Button.danger("desapuntarse_" + tarea.id(), "❌ No Asistir"));
        } else if (tarea.botonesTipo() == 2 && tarea.opciones() != null) {
            String[] ops = tarea.opciones().split("\\|");
            for (int i = 0; i < ops.length; i++) {
                botones.add(Button.primary("voto_" + i + "_" + tarea.id(), ops[i].trim()));
            }
        }
        return botones;
    }

    private Button crearBotonAnterior(int nuevaPagina, String filtro) {
        Button btnAnt = Button.secondary("cal:ant:" + nuevaPagina + ":" + filtro, "◀️ Anterior");
        return nuevaPagina == 0 ? btnAnt.asDisabled() : btnAnt;
    }

    private Button crearBotonSiguiente(int nuevaPagina, int total, String filtro) {
        Button btnSig = Button.secondary("cal:sig:" + nuevaPagina + ":" + filtro, "Siguiente ▶️");
        return nuevaPagina == total - 1 ? btnSig.asDisabled() : btnSig;
    }
}