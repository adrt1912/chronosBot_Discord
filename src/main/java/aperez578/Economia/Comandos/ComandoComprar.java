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

    private final Random random=new Random();

    private static final String MSG_FALTAN_MONEDAS = "** monedas. Te faltan **";

    private static final String PARAM_TEXTO = "texto";

    @Override
    public void ejecutar(ContextoComando ctx) {
        SlashCommandInteractionEvent event = ctx.getEventSlash();
        if (event.getGuild() == null) return;

        String userId = ctx.getIdAutor();
        int articulo = ctx.getParametroInt("articulo");
        long[] datosC = EconomiaBD.obtenerPerfilEconomia(userId);
        long saldo = datosC[0];
        long banco = datosC[1];

        // Enrutamiento de la lógica según el artículo comprado
        if (articulo >= 1 && articulo <= 5) comprarRolCosmetico(ctx, event, articulo, userId, saldo, banco);
        else if (articulo == 6) comprarMegafono(ctx, event, userId, saldo, banco);
        else if (articulo == 7) comprarCanalPrivado(ctx, event, userId, saldo, banco);
        else if (articulo == 8) comprarCajaSorpresa(ctx, userId, saldo, banco);
        else if (articulo == 9) comprarRolPersonalizado(ctx, event, userId, saldo, banco);
        else ctx.responder("❌ **Artículo No Encontrado** | Ese número no existe en el catálogo de la `/tienda`.");
    }

    private void comprarRolCosmetico(ContextoComando ctx, SlashCommandInteractionEvent event, int articulo, String userId, long saldo, long banco) {
        String[] idsRoles = {
                "111111111111111111", "222222222222222222", "333333333333333333", "444444444444444444", "555555555555555555"
        };
        long[] preciosRoles = {10000, 75000, 1500, 250000, 101000};

        long precio = preciosRoles[articulo - 1];
        String idRol = idsRoles[articulo - 1];
        Role rol = event.getGuild().getRoleById(idRol);

        if (rol == null)
            ctx.responder("❌ **Error técnico** | El rol de este artículo no está bien configurado en el bot. Avisa a un admin.");
        else if (saldo < precio)
            ctx.responder("💸 **Fondos Insuficientes** | Este artículo cuesta **" + precio + MSG_FALTAN_MONEDAS + (precio - saldo) + "**.");
        else if (Objects.requireNonNull(event.getMember()).getRoles().contains(rol))
            ctx.responder("🧐 **Objeto Duplicado** | ¡Ya tienes este rol! No tires el dinero.");
        else {
            // Cobro y asignación
            EconomiaBD.actualizarEconomia(userId, saldo - precio, banco);
            event.getGuild().addRoleToMember(event.getMember(), rol).queue();
            ctx.responder("✅ **¡Compra Completada!** | Has adquirido el rol **" + rol.getName() + "** por **" + precio + "** monedas.");
        }
    }

    private void comprarMegafono(ContextoComando ctx, SlashCommandInteractionEvent event, String userId, long saldo, long banco) {
        long precio = 1000000;
        String mensaje = ctx.getParametroString(PARAM_TEXTO);

        if (mensaje == null || mensaje.isEmpty())
            ctx.responder("❌ **Falta Parámetro** | Para usar el megáfono debes rellenar la opción `texto` con tu anuncio.");
        else if (saldo < precio)
            ctx.responder("💸 **Fondos Insuficientes** | El megáfono cuesta **" + precio + MSG_FALTAN_MONEDAS + (precio - saldo) + "**.");
        else {
            EconomiaBD.actualizarEconomia(userId, saldo - precio, banco);
            ctx.responder("📢 **¡Megáfono activado con éxito!** El anuncio ha sido enviado al canal.");
            event.getChannel().sendMessage("📢 **ANUNCIO PATROCINADO** 📢\n" +
                    "💬 *Mensaje de <@" + userId + ">:*\n" +
                    "» `" + mensaje + "`").queue();
        }
    }


    private void comprarCanalPrivado(ContextoComando ctx, SlashCommandInteractionEvent event, String userId, long saldo, long banco) {
        long precio = 3000000;
        if (saldo < precio)
            ctx.responder("💸 **Fondos Insuficientes** | Crear tu búnker privado cuesta **" + precio + MSG_FALTAN_MONEDAS + (precio - saldo) + "**.");
        else {

            EconomiaBD.actualizarEconomia(userId, saldo - precio, banco);
            Objects.requireNonNull(event.getGuild()).createTextChannel("🔒-búnker-de-" + event.getUser().getName())
                    .addRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, java.util.List.of(Permission.VIEW_CHANNEL))
                    .addMemberPermissionOverride(Objects.requireNonNull(event.getMember()).getIdLong(), java.util.List.of(Permission.VIEW_CHANNEL, Permission.MANAGE_CHANNEL), null)
                    .queue(channel -> ctx.responder("🔒 **¡Búnker Creado!** | Se ha abierto tu zona privada aquí: " + channel.getAsMention()));
        }
    }

    private void comprarCajaSorpresa(ContextoComando ctx, String userId, long saldo, long banco) {
        long precio = 1000;
        if (saldo < precio)
            ctx.responder("💸 **Fondos Insuficientes** | La Caja Sorpresa cuesta **" + precio + "** monedas.");
        else {
            int suerte = random.nextInt(1, 101);
            long premio;
            String frase;

            if (suerte <= 65) {
                premio = random.nextLong(100, 501);
                frase = "🗑️ **Premio Común** | La caja contenía chatarra y **" + premio + " monedas**. ¡Mala suerte!";
            } else if (suerte <= 95) {
                premio = random.nextLong(1500, 3001);
                frase = "✨ **¡Premio Raro!** | ¡Qué bien! La caja ocultaba un saco con **" + premio + " monedas**.";
            } else {
                premio = 15000;
                frase = "🔥 💎 **¡¡JACKPOT DE LA CAJA!!** 💎 🔥 | ¡Has destrozado las estadísticas! Te llevas **" + premio + " monedas**.";
            }
            // Unificado: Se actualiza la base de datos una sola vez en lugar de repetirse en cada bloque
            EconomiaBD.actualizarEconomia(userId, (saldo - precio) + premio, banco);
            ctx.responder("🎁 **Abriendo Caja Sorpresa...**\n" + frase);
        }
    }

    private void comprarRolPersonalizado(ContextoComando ctx, SlashCommandInteractionEvent event, String userId, long saldo, long banco) {
        long precio = 90000000;
        String nombreRol = ctx.getParametroString(PARAM_TEXTO);
        String colorHex = ctx.getParametroString("color");

        if (nombreRol == null || colorHex == null || !colorHex.startsWith("#")) {
            ctx.responder("❌ **Parámetros Inválidos** | Para tu rol personalizado debes añadir el nombre en `texto` y el color en `color` (Ejemplo: `#FF5733`).");
        }
        else if (saldo < precio) {
            ctx.responder("💸 **Fondos Insuficientes** | Diseñar tu propio rol cuesta **" + precio + "** monedas. ¡Te falta una fortuna!");
        }
        else {
            try {
                Color colorAsignar = Color.decode(colorHex);
                EconomiaBD.actualizarEconomia(userId, saldo - precio, banco);

                event.getGuild().createRole()
                        .setName(nombreRol)
                        .setColor(colorAsignar)
                        .queue(nuevoRol -> {
                            event.getGuild().addRoleToMember(Objects.requireNonNull(event.getMember()), nuevoRol).queue();
                            ctx.responder("🎭 **¡Rol de Autor Creado!** | Se ha creado e inyectado el rol **" + nombreRol + "** en tu perfil.");
                        });
            } catch (NumberFormatException nfe) {
                ctx.responder("❌ **Color Incorrecto** | El código HEX del color no es válido. Asegúrate de usar el formato `#RRGGBB`.");
            }
        }
    }

    @Override
    public SlashCommandData getDatosComando() {
        return Commands.slash("comprar", "Adquiere un artículo o servicio de la tienda de Chronos.")
                .addOption(OptionType.INTEGER, "articulo", "El número del artículo que quieres comprar (1-9).", true)
                .addOption(OptionType.STRING, PARAM_TEXTO, "Nombre del rol personalizado o texto para el megáfono.", false)
                .addOption(OptionType.STRING, "color", "Color en formato HEX (Ejemplo: #FF5733) para tu rol personalizado.", false);
    }
}