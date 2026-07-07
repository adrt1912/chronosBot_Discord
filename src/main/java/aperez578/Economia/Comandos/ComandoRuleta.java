package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Random;

public class ComandoRuleta implements Comando {

    private final Random random=new Random();

    private final static String PARAM_NEGRO = "negro";
    private final static String PARAM_VERDE="verde";
    private final static String PARAM_ROJO="rojo";

    public void ejecutar(ContextoComando ctx) {
        String userId = ctx.getIdAutor();
        long cantidad = ctx.getParametroLong("cantidad");
        String opcion = ctx.getParametroString("color");
        long[] datosC = EconomiaBD.obtenerPerfilEconomia(userId);

        // 1. Cláusulas de guarda para validaciones iniciales (Evitan anidamiento)
        if (cantidad <= 0) {
            ctx.responder("❌ **Error de Apuesta** | La cantidad a arriesgar debe ser mayor que 0. ¡No intentes engañar a la banca!");
            return;
        }

        if (datosC[0] < cantidad) {
            ctx.responder("💸 **Bancarrota** | No tienes suficientes monedas en tu cartera para realizar esta apuesta. ¡Ve a `/trabajar` primero!");
            return;
        }

        // 2. Determinación del color ganador
        String ganadorColor = determinarColorGanador();
        long cantidadGanada = datosC[0];

        // 3. Procesamiento de resultados
        if (opcion.equals(ganadorColor)) {
            if (!opcion.equals(PARAM_VERDE)) {
                cantidadGanada += cantidad;
                ctx.responder("🎉 **¡La ruleta se detiene!**\nHa salido el color **" + ganadorColor.toUpperCase() + "** y has acertado. ¡Has duplicado tu apuesta ganando **" + cantidad + "** monedas! 💰");
            } else {
                cantidadGanada += cantidad * 35L;
                ctx.responder("🟢 ✨ **¡¡EL PREMIO GORDO DEL CASINO!!** ✨ 🟢\n¡La bola ha caído milagrosamente en el **VERDE**! Has multiplicado tu apuesta por 35. ¡Te llevas **" + (cantidad * 35) + "** monedas de golpe de la banca! 🚀");
            }
        } else {
            cantidadGanada -= cantidad;
            String emojiGanador = obtenerEmojiColor(ganadorColor);
            ctx.responder("💀 **La banca gana** | La bola cayó en el " + emojiGanador + " **" + ganadorColor.toUpperCase() + "**. Has perdido tus **" + cantidad + "** monedas apostadas... ¡La suerte cambia en la próxima ronda!");
        }
        EconomiaBD.actualizarEconomia(userId, cantidadGanada, datosC[1]);
    }

    // Métodos auxiliares para limpiar el flujo principal
    private String determinarColorGanador() {
        int numA = random.nextInt(0, 1001);
        if (numA < 495) return PARAM_ROJO;
        if (numA < 990) return PARAM_NEGRO;
        return PARAM_VERDE;
    }

    private String obtenerEmojiColor(String color) {
        return switch (color){
            case PARAM_ROJO -> "🔴";
            case PARAM_NEGRO -> "⚫";
            default -> "🟢";
        };
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("ruleta", "Prueba tu suerte en la ruleta del casino apostando tus monedas.")
                .addOption(OptionType.INTEGER, "cantidad", "La cantidad de monedas que quieres arriesgar.", true)
                .addOptions(new net.dv8tion.jda.api.interactions.commands.build.OptionData(OptionType.STRING, "color", "El color al que quieres apostar.", true)
                        .addChoice(PARAM_ROJO+"🔴", PARAM_ROJO)
                        .addChoice(PARAM_NEGRO+"⚫", PARAM_NEGRO)
                        .addChoice( PARAM_VERDE+"🟢", PARAM_VERDE)
                );
    }
}