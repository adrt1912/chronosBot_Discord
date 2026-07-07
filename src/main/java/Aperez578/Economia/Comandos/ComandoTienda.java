package Aperez578.Economia.Comandos;

import Aperez578.Comando;
import Aperez578.ContextoComando;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public class ComandoTienda implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {

        // DISEÑO DEL ESCAPARATE

        String sb = "🏪 **LA TIENDA DE CHRONOS** 🏪\n" +
                "¡Gasta tus monedas acumuladas para conseguir prestigio en el servidor!\n" +
                "---------------------------------------------------\n\n" +

                //  Artículo 1
                "🔹 **[1] Rol VIP**\n" +
                "➔ 🪙 Precio: **10,000 monedas**\n" +
                "➔ *Beneficio:* Destaca tu nombre en la lista de usuarios con un color dorado exclusivo.\n\n" +

                //  Artículo 2
                "🔹 **[2] Magnate del Servidor**\n" +
                "➔ 🪙 Precio: **75,000 monedas**\n" +
                "➔ *Beneficio:* El rol definitivo para los verdaderos millonarios. ¡Desbloquea canales VIP!\n\n" +

                //  Artículo 3
                "🔹 **[3] Payaso Oficial**\n" +
                "➔ 🪙 Precio: **1,500 monedas**\n" +
                "➔ *Beneficio:* Consigues un rol de color rosa chillón. Ideal para auto-humillarte o para cuando lo pierdes todo en las tragaperras.\n\n" +

                //  Artículo 4
                "🔹 **[4] Rey del Casino**\n" +
                "➔ 🪙 Precio: **250,000 monedas**\n" +
                "➔ *Beneficio:* Un rol ultra exclusivo de color rojo neón para demostrar quién ha dominado la suerte y desplumado a Chronos.\n\n" +

                //  Artículo 5
                "🔹 **[5] Escudo Divino**\n" +
                "➔ 🪙 Precio: **101,000 monedas**\n" +
                "➔ *Beneficio:* Te otorga un rol celestial que demuestra tu inmunidad ante los intentos de atraco de otros usuarios.\n\n" +

                "📢 **[6] Megáfono Global** ➔ 🪙 **1,000,000 monedas**\n" +
                "➔ *Beneficio:* Envía un anuncio gigante fijado en este canal con el texto que tú quieras.\n" +
                "➔ *Uso:* `/comprar articulo: 6 texto: ¡Tu mensaje aquí!`\n-------------\n" +

                "🔒 **[7] Tu Canal Privado** ➔ 🪙 **3,000,000 monedas**\n" +
                "➔ *Beneficio:* Chronos creará un canal de texto secreto solo para ti. ¡Tú mandas allí!\n" +
                "➔ *Uso:* `/comprar articulo: 7`\n-------------\n" +

                "🎁 **[8] Caja Sorpresa (Lootbox)** ➔ 🪙 **1,000 monedas**\n" +
                "➔ *Beneficio:* ¡Prueba tu suerte! Puede tocarte desde una miseria hasta un premio de 15,000 monedas.\n" +
                "➔ *Uso:* `/comprar articulo: 8`\n-------------\n" +

                "🎭 **[9] Rol 100% Personalizado** ➔ 🪙 **90,000,000 monedas**\n" +
                "➔ *Beneficio:* Crea un rol desde cero con el nombre y color exacto que tú elijas.\n" +
                "➔ *Uso:* `/comprar articulo: 9 texto: Leyenda color: #FF5733`\n" +
                "---------------------------------------------------\n" +
                "🛍️ **¿Cómo comprar?** Usa el comando `/comprar cantidad: [número]` para adquirir tu artículo.";

        // Enviamos  el catálogo en un único y limpio mensaje
        ctx.responder(sb);
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("tienda", "Muestra el catálogo de roles disponibles en la tienda de Chronos.");
    }
}