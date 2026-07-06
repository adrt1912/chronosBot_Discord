package aperez578.Notificaciones.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;

import java.util.Comparator;
import java.util.List;

public class ComandoListar implements Comando {

    public void ejecutar(ContextoComando ctx){
        List<Tarea> tareaList= ConexionBD.getConexionBD().listarTareas(ctx.getIdAutor());
        tareaList=tareaList.stream().sorted(Comparator.comparing(Tarea::getFecha).thenComparing(Tarea::getHora)).toList();
        tareaList.forEach(tarea -> ctx.responder(tarea.toString()));
    }
}