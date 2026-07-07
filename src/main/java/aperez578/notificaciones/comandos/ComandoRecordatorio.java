package aperez578.notificaciones.comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.time.Instant;

public class ComandoRecordatorio implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {

        String mensaje= ctx.getParametroString("mensaje").trim();
        String tiempo= ctx.getParametroString("tiempo").trim().toLowerCase();
        long tiempoSumar;
        try {
            if(tiempo.endsWith("s")) tiempoSumar=Long.parseLong(tiempo.replace("s", ""));
            else if(tiempo.endsWith("m")) tiempoSumar=Long.parseLong(tiempo.replace("m",""))*60;
            else if(tiempo.endsWith("h")) tiempoSumar=Long.parseLong(tiempo.replace("h",""))*3600;
            else if(tiempo.endsWith("d")) tiempoSumar=Long.parseLong(tiempo.replace("d",""))*86400;
            else tiempoSumar = Long.parseLong(tiempo) * 60;
        } catch (NumberFormatException _) {
            ctx.responder("❌ **Formato de tiempo inválido**. Usa números seguidos de `m` (minutos), `h` (horas) o `d` (días). Ej: `45m`, `2h`.");
            return;
        }

        if(tiempoSumar!=0){
            long timestampFuturo= Instant.now().getEpochSecond()+tiempoSumar;

            boolean guardado=NotificacionesBD.guardarRecordatorio(ctx.getIdAutor(), ctx.getChanelId(), mensaje,timestampFuturo);
            if(guardado) ctx.responder("✅ ¡Recordatorio programado! Te avisaré por aquí a las <t:" + timestampFuturo + ":T>.");
            else ctx.responder("❌ Hubo un error interno al intentar programar el recordatorio.");
        }else ctx.responder("⚠️ El tiempo debe ser mayor que cero.");
    }

    @Override
    public SlashCommandData getDatosComando() {
        return  Commands.slash("recordatorio", "Te envía un aviso automático pasado un tiempo determinado")
                .addOption(OptionType.STRING, "tiempo", "Ejemplos: 45m (minutos), 2h (horas), 1d (días)", true)
                .addOption(OptionType.STRING, "mensaje", "Qué quieres que te recuerde el bot", true);
    }
}