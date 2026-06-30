package org.example;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main {
    public static void main(String[] args) {
        // Sacamos el token de las variables de entorno del sistema operativo
        String token = System.getenv("DISCORD_TOKEN");

        if (token == null || token.isEmpty()) {
            System.err.println("¡ERROR: No se ha encontrado la variable de entorno DISCORD_TOKEN!");
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(token);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        builder.build();
        System.out.println("¡Chronos está online!");
    }
}