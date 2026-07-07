package aperez578;

import aperez578.experiencia.comandos.ManejadorXP;
import aperez578.notificaciones.comandos.BotonesEventos;
import aperez578.notificaciones.comandos.PlanificadorAlarmas;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main() throws InterruptedException {
        String token = System.getenv("DISCORD_TOKEN");

        if (token == null || token.isEmpty()) logger.info("¡ERROR: No se ha encontrado la variable de entorno DISCORD_TOKEN!");
        else{
        JDABuilder builder = JDABuilder.createDefault(token);

        // Inicializamos el lector que ahora también guardará las estructuras de los comandos
        LectorDeComandos lector = new LectorDeComandos();

        builder.addEventListeners(lector);
        builder.addEventListeners(new ManejadorSlash(lector));
        builder.addEventListeners(BotonesEventos.getBotonesEventos());
        builder.addEventListeners(new ManejadorXP());

        JDA jda = builder.build().awaitReady();

        // Sincronizamos los comandos pidiéndoselos al lector
        for (Guild servidor : jda.getGuilds()) {
            servidor.updateCommands().addCommands(lector.getListaComandData()).queue();
        }

        // Inicializamos servicios de fondo
        ConexionBD.getConexionBD().crearTablasSiNoExisten();
        new PlanificadorAlarmas(jda).iniciar();

        logger.info("¡Chronos está online!");
    }
}
}