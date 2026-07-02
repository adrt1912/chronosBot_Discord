package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.Tarea;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Comparator;
import java.util.List;

public class ComandoListar implements Comando {

    public void ejecutar(MessageReceivedEvent event){
        List<Tarea> tareaList= ConexionBD.getConexionBD().listarTareas(event.getAuthor().getId());
        tareaList=tareaList.stream().sorted(Comparator.comparing(Tarea::getFecha).thenComparing(Tarea::getHora)).toList();
        tareaList.forEach(tarea -> event.getChannel().sendMessage(tarea.toString()).queue());
    }
}
