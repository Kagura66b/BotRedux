package Handlers;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Monitor extends ListenerAdapter {
    public Monitor(){

    }
    // TODO: 9/27/20  log join and send DM
    @Override
    public void onGuildJoin(@NotNull GuildJoinEvent event) {
        super.onGuildJoin(event);
    }
    // TODO: 9/27/20  log leave
    @Override
    public void onGuildLeave(@NotNull GuildLeaveEvent event) {
        super.onGuildLeave(event);
    }
    // TODO: 9/27/20  check channel, convert react emote to role, assign role if not assigned
    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        super.onMessageReactionAdd(event);
        if(event.getChannel().getId().equals("762168259318775829") && !event.getUser().isBot()){
            RoleHandler.addRole(event);
        }
    }
    // TODO: 9/27/20  check channel, convert react emote to role, assign role if not assigned
    @Override
    public void onMessageReactionRemove(@NotNull MessageReactionRemoveEvent event) {
        super.onMessageReactionRemove(event);
        if(event.getChannel().getId().equals("762168259318775829") && !event.getUser().isBot()){
            RoleHandler.removeRole(event);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        for(Message.Attachment attachment : event.getMessage().getAttachments()) {
            //attachment.downloadToFile("/home/kagera/" + attachment.getFileName()).thenAccept(file -> System.out.println("A file has been saved"));
        }
    }
}
