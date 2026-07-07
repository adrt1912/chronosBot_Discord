package aperez.economia.comandos;

import aperez.Comando;
import aperez.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ComandoTienda implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {

        // DISEÑO DEL ESCAPARATE
        String sb = """
                🏪 **LA TIENDA DE CHRONOS** 🏪
                ¡Gasta tus monedas acumuladas para conseguir prestigio en el servidor!
                ---------------------------------------------------
                
                🔹 **[1] Rol VIP**
                ➔ 🪙 Precio: **10,000 monedas**
                ➔ *Beneficio:* Destaca tu nombre en la lista de usuarios con un color dorado exclusivo.
                
                🔹 **[2] Magnate del Servidor**
                ➔ 🪙 Precio: **75,000 monedas**
                ➔ *Beneficio:* El rol definitivo para los verdaderos millonarios. ¡Desbloquea canales VIP!
                
                🔹 **[3] Payaso Oficial**
                ➔ 🪙 Precio: **1,500 monedas**
                ➔ *Beneficio:* Consigues un rol de color rosa chillón. Ideal para auto-humillarte o para cuando lo pierdes todo en las tragaperras.
                
                🔹 **[4] Rey del Casino**
                ➔ 🪙 Precio: **250,000 monedas**
                ➔ *Beneficio:* Un rol ultra exclusivo de color rojo neón para demostrar quién ha dominado la suerte y desplumado a Chronos.
                
                🔹 **[5] Escudo Divino**
                ➔ 🪙 Precio: **101,000 monedas**
                ➔ *Beneficio:* Te otorga un rol celestial que demuestra tu inmunidad ante los intentos de atraco de otros usuarios.
                
                📢 **[6] Megáfono Global** ➔ 🪙 **1,000,000 monedas**
                ➔ *Beneficio:* Envía un anuncio gigante fijado en este canal con el texto que tú quieras.
                ➔ *Uso:* `/comprar articulo: 6 texto: ¡Tu mensaje aquí!`
                -------------
                
                🔒 **[7] Tu Canal Privado** ➔ 🪙 **3,000,000 monedas**
                ➔ *Beneficio:* Chronos creará un canal de texto secreto solo para ti. ¡Tú mandas allí!
                ➔ *Uso:* `/comprar articulo: 7`
                -------------
                
                🎁 **[8] Caja Sorpresa (Lootbox)** ➔ 🪙 **1,000 monedas**
                ➔ *Beneficio:* ¡Prueba tu suerte! Puede tocarte desde una miseria hasta un premio de 15,000 monedas.
                ➔ *Uso:* `/comprar articulo: 8`
                -------------
                
                🎭 **[9] Rol 100% Personalizado** ➔ 🪙 **90,000,000 monedas**
                ➔ *Beneficio:* Crea un rol desde cero con el nombre y color exacto que tú elijas.
                ➔ *Uso:* `/comprar articulo: 9 texto: Leyenda color: #FF5733`
                ---------------------------------------------------
                🛍️ **¿Cómo comprar?** Usa el comando `/comprar cantidad: [número]` para adquirir tu artículo.""";

        // Enviamos el catálogo en un único y limpio mensaje
        ctx.responder(sb);
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("tienda", "Muestra el catálogo de roles disponibles en la tienda de Chronos.");
    }
}