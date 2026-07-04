package aperez578;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Sacamos el token de las variables de entorno del sistema operativo
        String token = System.getenv("DISCORD_TOKEN");

        if (token == null || token.isEmpty()) {
            System.err.println("¡ERROR: No se ha encontrado la variable de entorno DISCORD_TOKEN!");
            return;
        }

        JDABuilder builder = JDABuilder.createDefault(token);

        LectorDeComandos lector = new LectorDeComandos();
        builder.addEventListeners(lector); // Registramos el de texto normal
        builder.addEventListeners(new ManejadorSlash(lector));
        builder.addEventListeners(BotonesEventos.getBotonesEventos());

        // Hacemos el ÚNICO build y se conecta a Discord
        JDA jda = builder.build();

        // Obligamos al código a esperar a que el bot cargue al 100%
        jda.awaitReady();

        // 🔄 Recorre todos los servidores del bot e inyecta los comandos / al arrancar
        for (Guild servidor : jda.getGuilds()) {
            servidor.updateCommands()
                    .addCommands(
                            Commands.slash("lista", "Muestra los asistentes confirmados a un evento")
                                    .addOptions(new OptionData(OptionType.INTEGER, "id", "Escribe el ID o nombre del evento", true)
                                            .setAutoComplete(true)
                                    ),

                            Commands.slash("ping", "Pong de respuesta del bot"),

                            Commands.slash("crear-notificacion", "Crea un nuevo evento o encuesta")
                                    .addOption(OptionType.STRING, "titulo", "El título del evento", true)
                                    .addOption(OptionType.STRING, "fecha_hora", "Formato: DD/MM/AAAA HH:MM (Ej: 15/07/2026 18:00)", true)
                                    .addOptions(
                                            new OptionData(OptionType.INTEGER, "tipo_boton", "Elige el formato interactivo", true)
                                                    .addChoice("📅 Lista de Asistencia", 1)
                                                    .addChoice("📊 Encuesta de Opciones", 2)
                                                    .addChoice("❌ Sin Botones (Solo aviso)", 0)
                                    )
                                    .addOption(OptionType.ROLE, "rol", "Rol al que quieres hacer ping (Opcional)", false)
                                    .addOption(OptionType.STRING, "opciones", "Para encuestas. Separa con barras: Opción A | Opción B (Opcional)", false),

                            Commands.slash("calendario", "Muestra los próximos eventos del servidor")
                                    .addOption(OptionType.STRING, "filtro", "Filtra por: hoy, semana, mes o el título del evento (Opcional)", false),
                            Commands.slash("listar-notificaciones", "Muestra las notificaciones configuradas"),
                            Commands.slash("ayuda", "Muestra la lista de comandos disponibles"),
                            Commands.slash("eliminar-tarea", "Elimina una tarea según su ID")
                                    .addOptions(new OptionData(OptionType.INTEGER, "id", "El ID numérico del evento", true).setAutoComplete(true)),
                            Commands.slash("editar-notificacion", "Permite modificar un evento o encuesta existente")
                                    .addOptions(new OptionData(OptionType.INTEGER, "id", "El ID numérico del evento", true).setAutoComplete(true))
                                    .addOptions(new OptionData(OptionType.STRING, "titulo", "Nuevo título para el evento (Opcional)", false).setAutoComplete(true))
                                    .addOptions(new OptionData(OptionType.STRING, "fecha_hora", "Nueva fecha/hora: DD/MM/AAAA HH:MM (Opcional)", false).setAutoComplete(true)),
                            Commands.slash("configuracion", "Configura el canal de alertas global"),
                            Commands.slash("cerrar", "Cierra la encuesta de un evento, así no se registran más resultados")
                                    .addOptions(new OptionData(OptionType.INTEGER, "id", "El ID numérico del evento", true).setAutoComplete(true)),
                            Commands.slash("mis-eventos", "Muestra tu agenda personal de eventos"),
                            Commands.slash("listar-resultados", "Muestra los resultados finales de las votaciones"),
                            Commands.slash("recordatorio", "Te envía un aviso automático pasado un tiempo determinado")
                                    .addOption(OptionType.STRING, "tiempo", "Ejemplos: 45m (minutos), 2h (horas), 1d (días)", true)
                                    .addOption(OptionType.STRING, "mensaje", "Qué quieres que te recuerde el bot", true)
                            )
                    .queue();
        }
        // Inicializamos las tablas de la base de datos
        ConexionBD.getConexionBD().crearTablasSiNoExisten();

        // Encendemos el vigilante pasándole el objeto jda
        PlanificadorAlarmas planificador = new PlanificadorAlarmas(jda);
        planificador.iniciar();

        System.out.println("¡Chronos está online y sincronizado!");
    }
}