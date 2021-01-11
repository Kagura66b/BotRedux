package Handlers;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MessageHandler extends ListenerAdapter {
    public MessageHandler(){

    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        String[] messageArgs = event.getMessage().getContentRaw().split(" ");
        System.out.println("receivedMessage");
        if(messageArgs[0].equals("!StatCheck")){
            String[] newArgs = new String[messageArgs.length-1];
            for(int i = 0;i<messageArgs.length-1;i++){
                newArgs[i] = messageArgs[i+1];
            }
            event.getJDA().addEventListener(new StatsHandler(event));
        }
    }
}
