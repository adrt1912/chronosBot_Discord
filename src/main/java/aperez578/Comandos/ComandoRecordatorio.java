package aperez578.Comandos;

import aperez578.Comando;
import aperez578.ConexionBD;
import aperez578.ContextoComando;

import java.time.Instant;

public class ComandoRecordatorio implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {

        String mensaje= ctx.getParametroString("mensaje").trim();
        String tiempo= ctx.getParametroString("tiempo").trim().toLowerCase();
        long tiempoSumar=0;
        try {
            if(tiempo.endsWith("s")) tiempoSumar=Long.parseLong(tiempo.replace("s", ""));
            else if(tiempo.endsWith("m")) tiempoSumar=Long.parseLong(tiempo.replace("m",""))*60;
            else if(tiempo.endsWith("h")) tiempoSumar=Long.parseLong(tiempo.replace("h",""))*3600;
            else if(tiempo.endsWith("d")) tiempoSumar=Long.parseLong(tiempo.replace("d",""))*86400;
            else tiempoSumar = Long.parseLong(tiempo) * 60;
        } catch (NumberFormatException e) {
            ctx.responder("❌ **Formato de tiempo inválido**. Usa números seguidos de `m` (minutos), `h` (horas) o `d` (días). Ej: `45m`, `2h`.");
            return;
        }

        if(tiempoSumar!=0){
            long timestampFuturo= Instant.now().getEpochSecond()+tiempoSumar;

            boolean guardado=ConexionBD.getConexionBD().guardarRecordatorio(ctx.getIdAutor(), ctx.getChanelId(), mensaje,timestampFuturo);
            if(guardado) ctx.responder("✅ ¡Recordatorio programado! Te avisaré por aquí a las <t:" + timestampFuturo + ":T>.");
            else ctx.responder("❌ Hubo un error interno al intentar programar el recordatorio.");

        }else ctx.responder("⚠️ El tiempo debe ser mayor que cero.");
    }
}
