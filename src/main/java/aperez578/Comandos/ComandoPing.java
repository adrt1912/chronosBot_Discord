package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;

public class ComandoPing implements Comando {

    public void ejecutar(ContextoComando ctx){
       ctx.responder("Pong");
    }
}