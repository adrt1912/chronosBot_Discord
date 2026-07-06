package aperez578.Utilidad.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import aperez578.LectorDeComandos;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class ComandoAyuda implements Comando {

    private final LectorDeComandos lector;

    // Guardamos la referencia al lector de comandos central
    public ComandoAyuda(LectorDeComandos lector) {
        this.lector = lector;
    }

    @Override
    public void ejecutar(ContextoComando ctx) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("📖 Panel de Ayuda • Chronos Bot")
                .setColor(new Color(0x3498db)) // Azul Chronos
                .setDescription("¡Hola! Aquí tienes la guía de comandos automatizada. Ya no necesitas usar el prefijo `!`, ahora todos funcionan escribiendo **`/`** en el chat.\n\n");

        // Preparamos los contenedores de las categorías en el orden en que queremos que salgan
        Map<String, StringBuilder> categorias = new LinkedHashMap<>();
        categorias.put("Notificaciones", new StringBuilder("📅 **NOTIFICACIONES Y EVENTOS**\n"));
        categorias.put("Economia", new StringBuilder("💰 **ECONOMÍA Y BIFURCACIONES**\n"));
        categorias.put("Casino", new StringBuilder("🎰 **CASINO Y JUEGOS**\n"));
        categorias.put("Experiencia", new StringBuilder("📈 **SISTEMA DE XP Y NIVELES**\n"));
        categorias.put("Utilidad", new StringBuilder("⚙️ **UTILIDAD Y SISTEMA**\n"));

        // El motor dinámico: Recorremos los 26 comandos que lee el bot en vivo
        for (Comando cmd : lector.getMapComandos().values()) {
            SlashCommandData datos = cmd.getDatosComando();
            String nombre = datos.getName();
            String descripcion = datos.getDescription();
            String rutaPaquete = cmd.getClass().getPackageName(); // Ej: aperez578.Economia.Comandos

            // Clasificamos el comando buscando palabras clave en su estructura de carpetas
            String claveCategoria = "Utilidad"; // Categoría por defecto si no encaja
            if (rutaPaquete.contains("Notificaciones")) claveCategoria = "Notificaciones";
            else if (rutaPaquete.contains("Economia")) claveCategoria = "Economia";
            else if (rutaPaquete.contains("Casino")) claveCategoria = "Casino";
            else if (rutaPaquete.contains("Experiencia")) claveCategoria = "Experiencia";

            // Enganchamos el comando formateado a su sección correspondiente
            categorias.get(claveCategoria)
                    .append("🔹 `/").append(nombre).append("` ➜ ").append(descripcion).append("\n");
        }

        // Construimos el cuerpo del Embed pegando solo las secciones que tengan comandos activos
        for (StringBuilder sb : categorias.values()) {
            // Contamos las líneas: si tiene más de una, significa que tiene comandos dentro y no solo el título
            if (sb.toString().lines().count() > 1) embed.appendDescription(sb + "\n");
        }

        embed.setFooter("Chronos Bot • Actualizado automáticamente", ctx.getJDA().getSelfUser().getAvatarUrl())
                .setTimestamp(java.time.Instant.now());

        ctx.responderEmbed(embed.build());
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("ayuda", "Muestra la lista de comandos disponibles de forma dinámica.");
    }
}