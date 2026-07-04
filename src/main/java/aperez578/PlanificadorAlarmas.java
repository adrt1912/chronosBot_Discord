package aperez578;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlanificadorAlarmas {

    private static final Logger logger = LoggerFactory.getLogger(PlanificadorAlarmas.class);

    private final JDA jda;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public PlanificadorAlarmas(JDA jda) {
        this.jda = jda;
    }

    public void iniciar() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                long tiempoahora = Instant.now().getEpochSecond();
                ConexionBD bd = ConexionBD.getConexionBD();

                // 1️⃣ EMPIEZA YA
                List<Tarea> tareaList = bd.tareasAvisar(tiempoahora - 300, tiempoahora);
                enviarAviso(tareaList, "🚨 **¡EL EVENTO EMPIEZA YA!** 🚨");
                for (Tarea t : tareaList) bd.borrarTarea(t.getId());

                // 2️⃣ FALTA 1 HORA
                List<Tarea> avisoUnaHora = bd.tareasAvisar(tiempoahora + 3600, tiempoahora + 3660);
                enviarAviso(avisoUnaHora, "⏳ **Recordatorio: Falta 1 hora** ⏳");

                // 3️⃣ FALTA 1 DÍA
                List<Tarea> avisoUnDIa = bd.tareasAvisar(tiempoahora + 86400, tiempoahora + 86460);
                enviarAviso(avisoUnDIa, "📅 **Recordatorio: Falta 1 día** 📅");

                //recordatorios
                List<RecordatorioObj> recordatoriosVencidos = ConexionBD.getConexionBD().obtenerRecordatoriosVencidos(tiempoahora);
                for (RecordatorioObj rec : recordatoriosVencidos) {
                    TextChannel canal = jda.getTextChannelById(rec.getCanalid());
                    if (canal != null) canal.sendMessage("🔔 <@" + rec.getUsuarioid() + "> **¡Recordatorio!**\n> " + rec.getMensaje()).queue();
                    // Lo borramos inmediatamente para que no se vuelva a repetir
                    ConexionBD.getConexionBD().eliminarRecordatorio(rec.getId());
                }
            } catch (Throwable e) {
                logger.info("⚠️ Error en el bucle de alarmas: " + e.getMessage());
            }
        }, 0, 1, TimeUnit.SECONDS);


    }

    public void enviarAviso(List<Tarea> tareas, String text) {
        for (Tarea tarea : tareas) {
            // Recuperamos el canal original donde se creó la tarea
            TextChannel canalOriginal = jda.getTextChannelById(tarea.getCanalID());

            if (canalOriginal != null) {
                // 1. Buscamos si el servidor tiene un canal preferido global configurado
                String canalConfiguradoId = ConexionBD.getConexionBD().obtenerCanalAlertas(canalOriginal.getGuild().getId());
                TextChannel canalDeEnvio = canalOriginal; // Por defecto mandamos al original

                // 2. Si hay un canal global configurado, desviamos el paquete allí
                if (canalConfiguradoId != null) {
                    TextChannel canalGlobal = jda.getTextChannelById(canalConfiguradoId);
                    if (canalGlobal != null) canalDeEnvio = canalGlobal;
                }

                // 3. Preparamos el texto del ping (Mención de Rol)
                String avisoConMencion = text;
                if (!"NINGUNA".equals(tarea.getRol_id())) avisoConMencion = tarea.getRol_id() + " " + text;

                // 4. Reconstruimos el Embed de la alarma
                EmbedBuilder embed = new EmbedBuilder()
                        .setTitle(text)
                        .setColor(Color.RED)
                        .setDescription("¡Preparaos comunitarios, tenemos una nueva cita!")
                        .addField("📌 Evento:", tarea.getTitulo(), false)
                        .addField("👤 Organizador:", "<@" + tarea.getUserID() + ">", true)

                        .addField("⏰ Hora programada:", "<t:" + tarea.getTimestamp() + ":F>\n⏳ *Comienza: <t:" + tarea.getTimestamp() + ":R>*", false)

                        .setFooter("Chronos Bot • Vigilante del Tiempo", jda.getSelfUser().getAvatarUrl())
                        .setTimestamp(Instant.now());

                // 5. Empaquetamos la acción en el canal definitivo e inyectamos los botones
                MessageCreateAction accion = canalDeEnvio.sendMessage(avisoConMencion).addEmbeds(embed.build());

                BotonesEventos.getBotonesEventos().aplicarBotones(accion, tarea);

                accion.queue();
            }
        }
    }
}