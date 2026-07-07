package aperez578.experiencia.comandos; // Ajusta el package a tu carpeta exacta

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ManejadorXP extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ManejadorXP.class);

    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.isFromGuild() || event.isWebhookMessage()) return;

        String userId = event.getAuthor().getId();
        String guildId = event.getGuild().getId();
        long tiempoActual = System.currentTimeMillis();

        // Anti-spam de 60 segundos
        if (cooldowns.containsKey(userId) && (tiempoActual - cooldowns.get(userId) < 60000)) {
            return;
        }

        cooldowns.put(userId, tiempoActual);
        int xpGanada = random.nextInt(15, 26);

        int[] resultado = ExperienciaBD.ganarXP(userId, guildId, xpGanada);
        boolean haSubido = resultado[1] == 1;

        if (haSubido) {
            // Anuncio clásico de subida de nivel
            int nivelActual = resultado[0];
            event.getChannel().sendMessage("🎉 **¡Sube de Nivel!** | " + event.getAuthor().getAsMention() + " ha alcanzado el **Nivel " + nivelActual + "**. ¡Sigue así! ✨").queue();

            // COMPROBACIÓN Y ASIGNACIÓN DE ROLES DE RECOMPENSA
            gestionarRolRecompensa(event, nivelActual);
        }
    }

    // Metodo auxiliar para mantener limpia la complejidad cognitiva
    private void gestionarRolRecompensa(MessageReceivedEvent event, int nivelActual) {
        if (event.getMember() == null) return;

        String idRolRecompensa = obtenerIdRolPorNivel(nivelActual);
        if (idRolRecompensa == null) return;

        Role rol = event.getGuild().getRoleById(idRolRecompensa);
        if (rol == null || event.getMember().getRoles().contains(rol)) return;

        event.getGuild().addRoleToMember(event.getMember(), rol).queue(_ -> {
            // Avisamos en el chat de que ha conseguido el rol con éxito
            event.getChannel().sendMessage("🏅 **¡Rol Desbloqueado!** " + event.getAuthor().getAsMention() + " ha obtenido el rol honorífico **" + rol.getName() + "**.").queue();
        }, _ -> logger.info("❌ Error de permisos: El bot no tiene rango suficiente para otorgar el rol {}", rol.getName()));
    }

    private String obtenerIdRolPorNivel(int nivel) {
        return switch (nivel) {
            case 5 -> "555555555555555555";   // Rol para Nivel 5 (Ej: Aprendiz)
            case 10 -> "101010101010101010"; // Rol para Nivel 10 (Ej: Veterano)
            case 20 -> "202020202020202020"; // Rol para Nivel 20 (Ej: Leyenda)
            default -> null;
        };
    }
}