package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;

public class ComandoTienda implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        StringBuilder sb = new StringBuilder();

        // 🏪 DISEÑO DEL ESCAPARATE
        sb.append("🏪 **LA TIENDA DE CHRONOS** 🏪\n");
        sb.append("¡Gasta tus monedas acumuladas para conseguir prestigio en el servidor!\n");
        sb.append("---------------------------------------------------\n\n");

        // 🌟 Artículo 1
        sb.append("🔹 **[1] Rol VIP**\n");
        sb.append("➔ 🪙 Precio: **10,000 monedas**\n");
        sb.append("➔ *Beneficio:* Destaca tu nombre en la lista de usuarios con un color dorado exclusivo.\n\n");

        // 🌟 Artículo 2
        sb.append("🔹 **[2] Magnate del Servidor**\n");
        sb.append("➔ 🪙 Precio: **75,000 monedas**\n");
        sb.append("➔ *Beneficio:* El rol definitivo para los verdaderos millonarios. ¡Desbloquea canales VIP!\n\n");

        // 🌟 Artículo 3
        sb.append("🔹 **[3] Payaso Oficial**\n");
        sb.append("➔ 🪙 Precio: **1,500 monedas**\n");
        sb.append("➔ *Beneficio:* Consigues un rol de color rosa chillón. Ideal para auto-humillarte o para cuando lo pierdes todo en las tragaperras.\n\n");

        // 🌟 Artículo 4
        sb.append("🔹 **[4] Rey del Casino**\n");
        sb.append("➔ 🪙 Precio: **250,000 monedas**\n");
        sb.append("➔ *Beneficio:* Un rol ultra exclusivo de color rojo neón para demostrar quién ha dominado la suerte y desplumado a Chronos.\n\n");


        // 🌟 Artículo 5
        sb.append("🔹 **[5] Escudo Divino**\n");
        sb.append("➔ 🪙 Precio: **101,000 monedas**\n");
        sb.append("➔ *Beneficio:* Te otorga un rol celestial que demuestra tu inmunidad ante los intentos de atraco de otros usuarios.\n\n");

        sb.append("📢 **[6] Megáfono Global** ➔ 🪙 **1,000,000 monedas**\n");
        sb.append("➔ *Beneficio:* Envía un anuncio gigante fijado en este canal con el texto que tú quieras.\n");
        sb.append("➔ *Uso:* `/comprar articulo: 6 texto: ¡Tu mensaje aquí!`\n-------------\n");

        sb.append("🔒 **[7] Tu Canal Privado** ➔ 🪙 **3,000,000 monedas**\n");
        sb.append("➔ *Beneficio:* Chronos creará un canal de texto secreto solo para ti. ¡Tú mandas allí!\n");
        sb.append("➔ *Uso:* `/comprar articulo: 7`\n-------------\n");

        sb.append("🎁 **[8] Caja Sorpresa (Lootbox)** ➔ 🪙 **1,000 monedas**\n");
        sb.append("➔ *Beneficio:* ¡Prueba tu suerte! Puede tocarte desde una miseria hasta un premio de 15,000 monedas.\n");
        sb.append("➔ *Uso:* `/comprar articulo: 8`\n-------------\n");

        sb.append("🎭 **[9] Rol 100% Personalizado** ➔ 🪙 **90,000,000 monedas**\n");
        sb.append("➔ *Beneficio:* Crea un rol desde cero con el nombre y color exacto que tú elijas.\n");
        sb.append("➔ *Uso:* `/comprar articulo: 9 texto: Leyenda color: #FF5733`\n");

        sb.append("---------------------------------------------------\n");
        sb.append("🛍️ **¿Cómo comprar?** Usa el comando `/comprar cantidad: [número]` para adquirir tu artículo.");

        // Enviamos todo el catálogo en un único y limpio mensaje
        ctx.responder(sb.toString());
    }
}
