package aperez578;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;

public class ContextoComando {

    private SlashCommandInteractionEvent eventSlash;
    private ButtonInteractionEvent eventoBoton;

    // Constructor para cuando se usa "/"
    public ContextoComando(SlashCommandInteractionEvent event) {
        this.eventSlash = event;
        event.deferReply().queue();
    }

    public ContextoComando(net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent event) {
        this.eventoBoton = event;
    }

    public String getIdAutor(){
        return (eventSlash!=null) ? eventSlash.getUser().getId() : eventoBoton.getUser().getId();
    }
    public User getAutor(){
        return(eventSlash!=null) ? eventSlash.getUser(): eventoBoton.getUser();
    }

    public JDA getJDA(){
        return  (eventSlash!=null) ? eventSlash.getJDA() : eventoBoton.getJDA();
    }

    public void responder(String mensaje) {
        if (eventSlash != null) eventSlash.getHook().editOriginal(mensaje).queue();
         else if (eventoBoton != null) eventoBoton.reply(mensaje).queue();

    }

    //  Para obtener un número entero (como el ID del evento)
    public int getParametroInt( String nombreSlash) {
            return eventSlash.getOption(nombreSlash).getAsInt();
    }

    //  Para obtener texto (como el título o la fecha)
    public String getParametroString( String nombreSlash) {
            var opcion = eventSlash.getOption(nombreSlash);
            return (opcion != null) ? opcion.getAsString() : null;

    }

    public void responderEmbed(net.dv8tion.jda.api.entities.MessageEmbed embed) {
        if (eventSlash != null) eventSlash.getHook().editOriginalEmbeds(embed).queue();
         else if (eventoBoton != null) eventoBoton.replyEmbeds(embed).queue();
        }

    public String getChanelId(){
        return (eventSlash!=null) ? eventSlash.getChannelId() : eventoBoton.getChannelId();
    }

    //  Para saber si venimos de una barra diagonal (true) o de texto plano (false)
    public boolean esSlash() {
        return eventSlash != null;
    }

    //  Para comprobar si el usuario ha rellenado una cajita opcional en Discord
    public boolean tieneOpcion(String nombreSlash) {
        return eventSlash != null && eventSlash.getOption(nombreSlash) != null;
    }

    public String getParametroRolMention(String nombreSlash) {
        if (eventSlash != null && eventSlash.getOption(nombreSlash) != null) return eventSlash.getOption(nombreSlash).getAsRole().getAsMention();
        return "NINGUNA"; // Si no hay rol, devolvemos tu palabra clave por defecto
    }

    public GuildChannel getParametroCanal(String nombreSlash) {
            var opcion = eventSlash.getOption(nombreSlash);
            return (opcion != null) ? opcion.getAsChannel() : null;

    }
    public String getGuildId() {
        return (eventSlash!=null)? eventSlash.getGuild().getId() :         eventoBoton.getGuild().getId();
    }

    // 🎛 Para responder con un Embed y sus botones/componentes de forma unificada
    public void responderEmbedConComponentes(MessageEmbed embed, ActionRow... filas) {
        if (eventSlash != null) eventSlash.getHook().editOriginalEmbeds(embed).setComponents(filas).queue();
         else if (eventoBoton != null) eventoBoton.replyEmbeds(embed).setComponents(filas).queue();
    }


    public void enviarMensajesEfimeros(String mensaje){
      if (eventSlash!=null)  eventSlash.getHook().sendMessage(mensaje).setEphemeral(true).queue();
    }
}
