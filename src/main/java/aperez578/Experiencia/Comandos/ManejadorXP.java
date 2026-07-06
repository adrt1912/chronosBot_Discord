package aperez578.Experiencia.Comandos; // Ajusta el package a tu carpeta exacta

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ManejadorXP extends ListenerAdapter {

    private final Map<String, Long> cooldowns = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot() || !event.isFromGuild() || event.isWebhookMessage()) return;

        String userId = event.getAuthor().getId();
        String guildId = event.getGuild().getId();
        long tiempoActual = System.currentTimeMillis();

        // ⏱ Anti-spam de 60 segundos
        if (cooldowns.containsKey(userId)) {
            long tiempoPasado = tiempoActual - cooldowns.get(userId);
            if (tiempoPasado < 60000) return;
        }

        cooldowns.put(userId, tiempoActual);
        int xpGanada = random.nextInt(15, 26);

        int[] resultado = ExperienciaBD.ganarXP(userId, guildId, xpGanada);
        int nivelActual = resultado[0];
        boolean haSubido = resultado[1] == 1;

        if (haSubido) {
            // Anuncio clásico de subida de nivel
            event.getChannel().sendMessage("🎉 **¡Sube de Nivel!** | " + event.getAuthor().getAsMention() + " ha alcanzado el **Nivel " + nivelActual + "**. ¡Sigue así! ✨").queue();
            //  COMPROBACIÓN DE ROLES DE RECOMPENSA
            String idRolRecompensa = null;

            // Define qué ID de rol se da en cada nivel (Copia las IDs reales de tu Discord)
            if (nivelActual == 5) idRolRecompensa = "555555555555555555"; // Rol para Nivel 5 (Ej: Aprendiz)
            else if (nivelActual == 10) idRolRecompensa = "101010101010101010"; // Rol para Nivel 10 (Ej: Veterano)
            else if (nivelActual == 20) idRolRecompensa = "202020202020202020"; // Rol para Nivel 20 (Ej: Leyenda)


            // 3. Si el nivel actual tiene un rol asignado, se lo otorgamos
            if (idRolRecompensa != null && event.getMember() != null) {
                Role rol = event.getGuild().getRoleById(idRolRecompensa);

                if (rol != null) {
                    // Evitamos dárselo si por algún motivo ya lo tiene
                    if (!event.getMember().getRoles().contains(rol)) {
                        event.getGuild().addRoleToMember(event.getMember(), rol).queue(success -> {
                            // Avisamos en el chat de que ha conseguido el rol con éxito
                            event.getChannel().sendMessage("🏅 **¡Rol Desbloqueado!** " + event.getAuthor().getAsMention() + " ha obtenido el rol honorífico **" + rol.getName() + "**.").queue();
                        }, _ -> {
                            System.out.println("❌ Error de permisos: El bot no tiene rango suficiente para otorgar el rol " + rol.getName());
                        });
                    }
                }
            }
        }
    }
}