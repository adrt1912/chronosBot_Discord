package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;

public class ComandoBalance implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {

        String userID= ctx.getIdAutor();
        long[] datos= ConexionBD.getConexionBD().obtenerPerfilEconomia(userID);
        ctx.responder("💰 **Banco de Chronos** | Tienes un total de **" + datos[0] + "** monedas resguardadas en tu cuenta.");    }
}