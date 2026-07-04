package aperez578;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;

public class ManejadorSlash extends ListenerAdapter {

    private final LectorDeComandos lectorMensajes;

    // Le pedimos el lector en el constructor para poder usar su MAP de comandos
    public ManejadorSlash(LectorDeComandos lectorMensajes) {
        this.lectorMensajes = lectorMensajes;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // 🚀 Le mandamos el nombre del comando y el evento al despachador central
        lectorMensajes.despacharSlash(event.getName(), event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        // 1. Verificamos si están interactuando con la cajita "id" de nuestros comandos admitidos
        if (event.getFocusedOption().getName().equals("id")) {

            // 2. Traemos la lista de tareas activas de este canal (igual que hace tu calendario)
            List<Tarea> tareas = ConexionBD.getConexionBD().listarTareasServer(event.getChannel().getId());

            // 3. Capturamos lo que el usuario está escribiendo en este milisegundo
            String loQueEstaEscribiendo = event.getFocusedOption().getValue().toLowerCase();

            // 4. Filtramos las tareas: mostramos solo las que coincidan con el ID o con el título
            List<Command.Choice> opcionesMenu = tareas.stream()
                    .filter(t -> String.valueOf(t.getId()).contains(loQueEstaEscribiendo)
                            || t.getTitulo().toLowerCase().contains(loQueEstaEscribiendo))
                    .map(t -> {
                        // Creamos el texto visual que verá el usuario en el menú desplegable de Discord
                        String textoVisual = "📌 [ID: " + t.getId() + "] " + t.getTitulo();

                        // Si el nombre es demasiado largo, Discord lo corta, así que lo limitamos por seguridad
                        if (textoVisual.length() > 100) textoVisual = textoVisual.substring(0, 96) + "...";

                        // Retornamos la opción: Nombre visual -> Valor real (el ID numérico)
                        return new Command.Choice(textoVisual, t.getId());
                    })
                    .limit(25) // Discord solo permite mostrar un máximo de 25 opciones a la vez
                    .toList();

            // 5. Le escupimos las opciones de vuelta a la pantalla del usuario al instante
            event.replyChoices(opcionesMenu).queue();
        }
    }
}