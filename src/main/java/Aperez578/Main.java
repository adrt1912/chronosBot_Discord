package Aperez578;

import Aperez578.Experiencia.Comandos.ManejadorXP;
import Aperez578.Notificaciones.Comandos.BotonesEventos;
import Aperez578.Notificaciones.Comandos.PlanificadorAlarmas;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String token = System.getenv("DISCORD_TOKEN");

        if (token == null || token.isEmpty()) System.err.println("¡ERROR: No se ha encontrado la variable de entorno DISCORD_TOKEN!");
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

        System.out.println("¡Chronos está online!");
    }
}
}