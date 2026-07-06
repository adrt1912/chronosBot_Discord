package aperez578.Notificaciones.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.Color;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ComandoCalendario implements Comando {

    @Override
    public void ejecutar(ContextoComando ctx) {
        String filtro = ctx.getParametroString("filtro");
        if (filtro == null || filtro.trim().isEmpty()) filtro = "all";
        else filtro = filtro.trim();

        // Cargamos la lista filtrada de la página 0
        List<Tarea> tareaList = obtenerTareasFiltradas(ctx.getChanelId(), filtro);

        if (tareaList.isEmpty()) {
            EmbedBuilder embedVacio = new EmbedBuilder()
                    .setTitle("📅 Calendario Vacío")
                    .setColor(Color.GRAY)
                    .setDescription("No hay eventos programados que coincidan con tu búsqueda en este canal.");
            ctx.responderEmbed(embedVacio.build());
            return;
        }

        // Renderizamos la primera página (índice 0)
        enviarPaginaCalendario(ctx, tareaList, 0, filtro);
    }

    // 🌟 MÉTODO MAESTRO: Filtra las tareas de forma idéntica para el comando y los botones
    public static List<Tarea> obtenerTareasFiltradas(String channelId, String filtro) {
        List<Tarea> lista = ConexionBD.getConexionBD().listarTareasServer(channelId);
        lista = lista.stream()
                .sorted(Comparator.comparing(Tarea::getFecha).thenComparing(Tarea::getHora))
                .toList();

        if (filtro.equalsIgnoreCase("all")) return lista;

        switch (filtro.toLowerCase()) {
            case "hoy" -> {return lista.stream().filter(t -> Objects.equals(t.getFecha(), LocalDate.now())).toList();}
            case "mañana", "manana" -> {return lista.stream().filter(t -> Objects.equals(t.getFecha(), LocalDate.now().plusDays(1))).toList();}
            case "semana" -> {
                LocalDate hoy = LocalDate.now();
                return lista.stream().filter(t -> !t.getFecha().isBefore(hoy) && !t.getFecha().isAfter(hoy.plusDays(7))).toList();
            }
            case "mes", "mensual" -> {
                LocalDate hoy = LocalDate.now();
                return lista.stream().filter(t -> !t.getFecha().isBefore(hoy) && !t.getFecha().isAfter(hoy.plusMonths(1))).toList();
            }
            default ->{
            final String f = filtro.toLowerCase();
            return lista.stream().filter(t -> t.getTitulo().toLowerCase().contains(f)).toList();}
        }
    }

    // 🖼️ GESTOR DE DISEÑO: Construye el mensaje con el Embed y las dos filas de botones
    public static void enviarPaginaCalendario(ContextoComando ctx, List<Tarea> tareas, int pagina, String filtro) {
        Tarea tarea = tareas.get(pagina);
        int total = tareas.size();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📌 " + tarea.getTitulo())
                .setColor(new Color(0x3498db))
                .addField("🆔 ID del Evento:", "`" + tarea.getId() + "`", true)
                .addField("👤 Organiza:", "<@" + tarea.getUserID() + ">", true)
                .addField("⏰ Fecha y Hora (Tu hora local):", "<t:" + tarea.getTimestamp() + ":F> (<t:" + tarea.getTimestamp() + ":R>)", false)
                .setFooter("Evento " + (pagina + 1) + " de " + total + " • Chronos Bot", ctx.getJDA().getSelfUser().getAvatarUrl());

        List<ActionRow> filasDeComponentes = new ArrayList<>();

        // Fila 1: Botones operativos de la tarea (Asistir / Votar)
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
        if (!botonesOperativos.isEmpty()) filasDeComponentes.add(ActionRow.of(botonesOperativos));

        // Fila 2: Botones de navegación (Anterior / Siguiente)
        List<Button> navegacion = new ArrayList<>();

        // Botón Anterior: Deshabilitado si estamos en la página 0
        Button btnAnt = Button.secondary("cal:ant:" + pagina + ":" + filtro, "◀️ Anterior");
        if (pagina == 0) btnAnt = btnAnt.asDisabled();
        navegacion.add(btnAnt);

        // Botón Siguiente: Deshabilitado si estamos en la última página
        Button btnSig = Button.secondary("cal:sig:" + pagina + ":" + filtro, "Siguiente ▶️");
        if (pagina == total - 1) btnSig = btnSig.asDisabled();
        navegacion.add(btnSig);

        filasDeComponentes.add(ActionRow.of(navegacion));

        // Despachamos el mensaje completo con todas sus filas inyectadas
        ctx.responderEmbedConComponentes(embed.build(), filasDeComponentes.toArray(new ActionRow[0]));
    }
}