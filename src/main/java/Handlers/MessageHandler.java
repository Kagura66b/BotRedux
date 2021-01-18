package Handlers;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageHandler extends ListenerAdapter {
    public MessageHandler(){

    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().split(" ");
        if(messageArgs[0].toLowerCase().equals("!statcheck")){
            if(event.getMessage().getContentRaw().toLowerCase().equals("!statcheck help")){
                event.getChannel().sendMessage("This command is used to check the odds of success given either 2 dice pools or a dice pool and a threshold. To test against a threshold use -1 as the limit in the second dataset. Current test parameters are 10000 reps").queue();
            }else{
                event.getJDA().addEventListener(new DMStatsHandler(event));
            }
        }
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().split(" ");
        if(messageArgs[0].toLowerCase().equals("!statcheck")){
            if(event.getMessage().getContentRaw().toLowerCase().equals("!statcheck help")){
                event.getChannel().sendMessage("This command is used to check the odds of success given either 2 dice pools or a dice pool and a threshold. To test against a threshold use -1 as the limit in the second dataset. Current test parameters are 10000 reps").queue();
            }else{
                event.getJDA().addEventListener(new GuildStatsHandler(event));
            }
        }
    }
}
