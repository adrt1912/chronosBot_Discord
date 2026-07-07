package aperez578.notificaciones.comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Comparator;
import java.util.List;

public class ComandoListar implements Comando {

    public void ejecutar(ContextoComando ctx){
        List<Tarea> tareaList= NotificacionesBD.listarTareas(ctx.getIdAutor());
        tareaList=tareaList.stream().sorted(Comparator.comparing(Tarea::getFecha).thenComparing(Tarea::getHora)).toList();
        tareaList.forEach(tarea -> ctx.responder(tarea.toString()));
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("lista", "Muestra los asistentes confirmados a un evento")
                .addOptions(new OptionData(OptionType.INTEGER, "id", "Escribe el ID o nombre del evento", true)
                        .setAutoComplete(true)
                );
    }
}