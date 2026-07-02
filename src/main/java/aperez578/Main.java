package aperez578;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
     static void main(String[] args) throws InterruptedException {
        // Sacamos el token de las variables de entorno del sistema operativo
        String token = System.getenv("DISCORD_TOKEN");

        if (token == null || token.isEmpty()) System.err.println("¡ERROR: No se ha encontrado la variable de entorno DISCORD_TOKEN!");
         else {
            JDABuilder builder = JDABuilder.createDefault(token);
            builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
            builder.addEventListeners(new LectorDeComandos());

            // 1. Aquí se hace el ÚNICO build y se conecta a Discord
            JDA jda = builder.build();

            // 2. Obligamos al código a esperar a que el bot cargue al 100% en Discord
            jda.awaitReady();

            // 3. Inicializamos las tablas de la base de datos
            ConexionBD.getConexionBD().crearTablasSiNoExisten();

            // 4. Encendemos el vigilante pasándole el objeto jda
            PlanificadorAlarmas planificador = new PlanificadorAlarmas(jda);
            planificador.iniciar();

            System.out.println("¡Chronos está online!");
        }
    }
}