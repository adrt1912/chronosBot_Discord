package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ComandoTransferir implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        String idIniciar=ctx.getIdAutor();
        long[] datosUsuario= EconomiaBD.obtenerPerfilEconomia(idIniciar);
        String idReceptor=ctx.getParametroString("usuario");
        long[] datosReceptor=EconomiaBD.obtenerPerfilEconomia(idReceptor);

        long cantidad=ctx.getParametroInt("cantidad");
        if (idReceptor.equals(idIniciar)) ctx.responder("🧐 **Operación Inválida** | ¿Intentas hacerte una transferencia a ti mismo? ¡Eso es solo cambiar el dinero de bolsillo!");
        else if (ctx.getParametroUser("usuario").isBot()) ctx.responder("🤖 **Operación Cancelada** | ¿Intentas enviarle dinero a un bot? Ellos se alimentan de electricidad, no de monedas.");
        else if(cantidad<=0)ctx.responder("❌ **Cantidad Errónea** | Debes especificar una cantidad mayor que 0 para realizar un envío de dinero válido.");
        else if (datosUsuario[0] < cantidad)ctx.responder("💸 **Fondos Insuficientes** | No tienes suficientes monedas para esta transferencia. Tu saldo actual es de **" + datosUsuario[0] + "** monedas.");
        else{
            EconomiaBD.actualizarEconomia(idIniciar,datosUsuario[0]-cantidad,datosUsuario[1]);
            EconomiaBD.actualizarEconomia(idReceptor,datosReceptor[0]+cantidad,datosReceptor[1]);
            ctx.responder("");
            ctx.responder("✅ **¡Transferencia Exitosa!** 💸\n" +
                    "Has enviado **" + cantidad + "** monedas correctamente a <@" + idReceptor + ">.\n\n" +
                    "💼 *Tu nuevo saldo:* **" + (datosUsuario[0] - cantidad) + "** monedas.");
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("transferir", "Transfiere una cantidad de tus monedas a otro usuario.")
                .addOption(OptionType.USER, "usuario", "El usuario que recibirá las monedas.", true)
                .addOption(OptionType.INTEGER, "cantidad", "La cantidad de monedas a transferir.", true);
    }
}
