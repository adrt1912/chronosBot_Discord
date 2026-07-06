package aperez578.Economia.Comandos;

import aperez578.Comando;
import aperez578.ContextoComando;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.awt.Color;
import java.util.Objects;
import java.util.Random;

public class ComandoComprar implements Comando {
    @Override
    public void ejecutar(ContextoComando ctx) {
        // Obtenemos el evento original de JDA para interactuar con Discord
        SlashCommandInteractionEvent event =  ctx.getEventSlash();
        if (event.getGuild() == null) return;

        String userId = ctx.getIdAutor();
        int articulo = ctx.getParametroInt("articulo");
        long[] datosC =EconomiaBD.obtenerPerfilEconomia(userId);
        long saldo = datosC[0];

        // ⚠ REEMPLAZA ESTAS IDS POR LAS IDS REALES DE LOS ROLES DE TU PROPIO SERVIDOR
        String[] idsRoles = {
                "111111111111111111", // ID Rol 1 (VIP)
                "222222222222222222", // ID Rol 2 (Magnate)
                "333333333333333333", // ID Rol 3 (Payaso)
                "444444444444444444", // ID Rol 4 (Rey del Casino)
                "555555555555555555"  // ID Rol 5 (Escudo Divino)
        };

        // Precios exactamente alineados con tu ComandoTienda
        long[] preciosRoles = {10000, 75000, 1500, 250000, 101000};

        //  CASO 1 AL 5: Roles Cosméticos Estándar
        if (articulo >= 1 && articulo <= 5) {
            long precio = preciosRoles[articulo - 1];
            String idRol = idsRoles[articulo - 1];
            Role rol = Objects.requireNonNull(event.getGuild()).getRoleById(idRol);

            if (rol == null)
                ctx.responder("❌ **Error técnico** | El rol de este artículo no está bien configurado en el bot. Avisa a un admin.");
            else if (saldo < precio)
                ctx.responder("💸 **Fondos Insuficientes** | Este artículo cuesta **" + precio + "** monedas. Te faltan **" + (precio - saldo) + "**.");
            else if (Objects.requireNonNull(event.getMember()).getRoles().contains(rol))
                ctx.responder("🧐 **Objeto Duplicado** | ¡Ya tienes este rol! No tires el dinero.");
            else {
                // Cobramos y otorgamos el rol
                EconomiaBD.actualizarEconomia(userId, saldo - precio, datosC[1]);
                event.getGuild().addRoleToMember(event.getMember(), rol).queue();
                ctx.responder("✅ **¡Compra Completada!** | Has adquirido el rol **" + rol.getName() + "** por **" + precio + "** monedas.");
            }
        }

        //  CASO 6: Megáfono Global (1,000,000 monedas)
        else if (articulo == 6) {
            long precio = 1000000;
            String mensaje = ctx.getParametroString("texto");

            if (mensaje == null || mensaje.isEmpty())
                ctx.responder("❌ **Falta Parámetro** | Para usar el megáfono debes rellenar la opción `texto` con tu anuncio.");
            else if (saldo < precio)
                ctx.responder("💸 **Fondos Insuficientes** | El megáfono cuesta **" + precio + "** monedas. Te faltan **" + (precio - saldo) + "**.");
            else {
                EconomiaBD.actualizarEconomia(userId, saldo - precio, datosC[1]);
                ctx.responder("📢 **¡Megáfono activado con éxito!** El anuncio ha sido enviado al canal.");

                // Envía el anuncio formal al canal de texto
                event.getChannel().sendMessage("📢 **ANUNCIO PATROCINADO** 📢\n" +
                        "💬 *Mensaje de <@" + userId + ">:*\n" +
                        "» `" + mensaje + "`").queue();
            }
        }

        // 🔒 CASO 7: Canal Privado (30,000 monedas)
        else if (articulo == 7) {
            long precio = 30000;
            if (saldo < precio) ctx.responder("💸 **Fondos Insuficientes** | Crear tu búnker privado cuesta **" + precio + "** monedas. Te faltan **" + (precio - saldo) + "**.");
         else {
            EconomiaBD.actualizarEconomia(userId, saldo - precio, datosC[1]);

            // Crea un canal de texto que oculta la vista al rol @everyone y le da acceso total al comprador
            Objects.requireNonNull(event.getGuild()).createTextChannel("🔒-búnker-de-" + event.getUser().getName())
                    .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, java.util.List.of(Permission.VIEW_CHANNEL))
                    .addMemberPermissionOverride(Objects.requireNonNull(event.getMember()).getIdLong(), java.util.List.of(Permission.VIEW_CHANNEL, Permission.MANAGE_CHANNEL), null)
                    .queue(channel -> ctx.responder("🔒 **¡Búnker Creado!** | Se ha abierto tu zona privada aquí: " + channel.getAsMention()));
        }
    }

        //  CASO 8: Caja Sorpresa (1,000 monedas)
        else if (articulo == 8) {
            long precio = 1000;
            if (saldo < precio)
                ctx.responder("💸 **Fondos Insuficientes** | La Caja Sorpresa cuesta **" + precio + "** monedas.");
            else {

                Random r = new Random();
                int suerte = r.nextInt(1, 101); // Número del 1 al 100
                long premio;
                String frase;

                if (suerte <= 65) { // 65% probabilidad: Pierdes o recuperas poco
                    premio = r.nextLong(100, 501);
                    frase = "🗑️ **Premio Común** | La caja contenía chatarra y **" + premio + " monedas**. ¡Mala suerte!";
                    EconomiaBD.actualizarEconomia(userId, (saldo - precio) + premio, datosC[1]);
                } else if (suerte <= 95) { // 30% probabilidad: Premio medio chulo
                    premio = r.nextLong(1500, 3001);
                    frase = "✨ **¡Premio Raro!** | ¡Qué bien! La caja ocultaba un saco con **" + premio + " monedas**.";
                   EconomiaBD.actualizarEconomia(userId, (saldo - precio) + premio, datosC[1]);
                } else { // 5% probabilidad: El gordo de la lootbox
                    premio = 15000;
                    frase = "🔥 💎 **¡¡JACKPOT DE LA CAJA!!** 💎 🔥 | ¡Has destrozado las estadísticas! Te llevas **" + premio + " monedas**.";
                   EconomiaBD.actualizarEconomia(userId, (saldo - precio) + premio, datosC[1]);
                }

                ctx.responder("🎁 **Abriendo Caja Sorpresa...**\n" + frase);
            }
        }

        //  CASO 9: Rol Personalizado de Autor (90,000,000 monedas)
        else if (articulo == 9) {
            long precio = 90000000;
            String nombreRol = ctx.getParametroString("texto");
            String colorHex = ctx.getParametroString("color");

            if (nombreRol == null || colorHex == null || !colorHex.startsWith("#"))
                ctx.responder("❌ **Parámetros Inválidos** | Para tu rol personalizado debes añadir el nombre en `texto` y el color en `color` (Ejemplo: `#FF5733`).");
            else if (saldo < precio) ctx.responder("💸 **Fondos Insuficientes** | Diseñar tu propio rol cuesta **" + precio + "** monedas. ¡Te falta una fortuna!");
             else {
                try {
                    Color colorAsignar = Color.decode(colorHex);
                    EconomiaBD.actualizarEconomia(userId, saldo - precio, datosC[1]);

                    // JDA crea el rol en el servidor y luego se lo inyecta al usuario
                    event.getGuild().createRole()
                            .setName(nombreRol)
                            .setColor(colorAsignar)
                            .queue(nuevoRol -> {
                                event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), nuevoRol).queue();
                                ctx.responder("🎭 **¡Rol de Autor Creado!** | Se ha creado e inyectado el rol **" + nombreRol + "** en tu perfil.");
                            });
                } catch (NumberFormatException e) {
                    ctx.responder("❌ **Color Incorrecto** | El código HEX del color no es válido. Asegúrate de usar el formato `#RRGGBB`.");
                }
            }
        }

        else ctx.responder("❌ **Artículo No Encontrado** | Ese número no existe en el catálogo de la `/tienda`.");

    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("comprar", "Adquiere un artículo o servicio de la tienda de Chronos.")
                .addOption(OptionType.INTEGER, "articulo", "El número del artículo que quieres comprar (1-9).", true)
                .addOption(OptionType.STRING, "texto", "Nombre del rol personalizado o texto para el megáfono.", false)
                .addOption(OptionType.STRING, "color", "Color en formato HEX (Ejemplo: #FF5733) para tu rol personalizado.", false);
    }
}