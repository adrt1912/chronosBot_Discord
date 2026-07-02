package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.Tarea;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class ComandoMisEventos implements Comando {

    @Override
    public void ejecutar(MessageReceivedEvent event) {
        String autorid=event.getAuthor().getId();
        List<Tarea> listTareaApuntadas= ConexionBD.getConexionBD().listarTareasAsistidas(autorid);
        List<Tarea> listTareasOrganizadas=ConexionBD.getConexionBD().listarTareas(autorid);
        if(listTareaApuntadas.isEmpty()&&listTareasOrganizadas.isEmpty())event.getChannel().sendMessage("📭 <@" + autorid + ">, tu agenda está completamente vacía actualmente.").queue();
        else{
            StringBuilder mensajeFinal = new StringBuilder();
            mensajeFinal.append("📅 **__TU AGENDA PERSONAL DE EVENTOS__** 📅\n")
                    .append("Hola <@").append(autorid).append(">, aquí tienes tus tarjetas de actividad:\n\n");

            //  EVENTOS CREADOS POR EL USUARIO
            mensajeFinal.append("👑 **EVENTOS ORGANIZADOS POR TI**\n");
            if (listTareasOrganizadas.isEmpty()) mensajeFinal.append("> *No estás organizando ningún evento ahora mismo.*\n\n");
            else {
                for (Tarea t : listTareasOrganizadas) {
                    // 🌟 Tarjeta de texto usando '>>>' para crear el bloque contenedor
                    mensajeFinal.append(">>> 📌 **").append(t.getTitulo()).append("**\n")
                            .append("🆔 **ID del Evento:** `").append(t.getId()).append("`\n")
                            .append("⏰ **Fecha y Hora:** `").append(t.getFecha()).append("` a las `").append(t.getHora()).append("` hs\n")
                            .append("───────────────────────────────\n\n"); // Separador interno
                }
            }

            //  EVENTOS A LOS QUE ASISTE
            mensajeFinal.append("👥 **TUS PRÓXIMAS CITAS (APUNTADO)**\n");

            // Filtramos para no mostrar duplicados si asiste a un evento que él mismo creó
            List<Tarea> asistidosFiltrados = listTareaApuntadas.stream()
                    .filter(t -> !t.getUserID().equals(autorid))
                    .toList();

            if (asistidosFiltrados.isEmpty()) mensajeFinal.append("> *No estás apuntado a eventos de otros usuarios.*\n\n");
             else {
                for (Tarea t : asistidosFiltrados) {
                    mensajeFinal.append(">>> ✅ **").append(t.getTitulo()).append("**\n")
                            .append("👤 **Organiza:** <@").append(t.getUserID()).append(">\n")
                            .append("⏰ **Fecha y Hora:** `").append(t.getFecha()).append("` a las `").append(t.getHora()).append("` hs\n")
                            .append("───────────────────────────────\n\n");
                }
            }

            // 4. Enviamos el bloque de tarjetas por Mensaje Privado (DM)
            event.getAuthor().openPrivateChannel().queue(
                    privateChannel -> {
                        privateChannel.sendMessage(mensajeFinal.toString()).queue();
                        // Confirmación en el canal público
                        event.getChannel().sendMessage("📬 <@" + autorid + ">, te he enviado tus tarjetas de eventos por privado.").queue();
                    },
                    throwable -> {
                        // Si el privado falla (DMs cerrados), se lo enviamos al canal de texto normal
                        event.getChannel().sendMessage("⚠️ <@" + autorid + ">, tienes los mensajes privados cerrados. Te dejo tus tarjetas aquí:\n\n" + mensajeFinal).queue();
                    }
            );
        }
    }
}
