package Main;

import Handlers.CraftHandler;
import Handlers.ItemHandler;
import Handlers.LootHandler;
import Handlers.ResponseHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.util.Random;

public class MessageHandler extends ListenerAdapter {

    CSVImport dataTable;
    CraftHandler craftHandler;
    LootHandler lootHandler;
    JDA jda;

    MessageHandler(CSVImport constructDataTable, ResponseHandler responseHandler, JDA importJDA) throws IOException {
        dataTable = constructDataTable;
        jda = importJDA;
        craftHandler = new CraftHandler(dataTable, responseHandler, jda);
        lootHandler = new LootHandler(dataTable);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //super.onMessageReceived(event);

        if(event.getMessage().getContentRaw().contains("Melissa, Willow")){
            event.getMessage().delete().queue();
            //event.getChannel().sendMessage("Stop spamming your charlist cost").queue();
        }
        if(event.getMessage().getContentRaw().contains("!char list") && event.getAuthor().getName().contains("costblade")){
            event.getMessage().delete().queue();
            //event.getChannel().sendMessage("Stop spamming your charlist cost").queue();
        }
        if(event.getMessage().getContentRaw().equals("!ch") && event.getAuthor().getName().contains("costblade")){
            event.getMessage().delete().queue();
            event.getChannel().sendMessage("Shut up mum").queue();
        }

        if(event.getAuthor().isBot()){
            return;
        }
        String command = event.getMessage().getContentRaw();
        command = command.toLowerCase();
        if(command.startsWith("!!loot ")){
            lootHandler.processCommand(command, event);
        }else if(command.startsWith("!!item ")) {
            ItemHandler.getItem(command, event, dataTable);
        }else if(command.startsWith("!!craft ")||command.startsWith("!!crafting ")){
            craftHandler.processCommand(command, event);
        }else if(command.startsWith("?8ball")) {
            Random rand = new Random();
            int check = rand.nextInt(3);
            if(command.contains("pokemon")){
                event.getChannel().sendMessage("https://cdn.discordapp.com/attachments/544697589623160832/679075992442109972/167cb16fc301dec313d8ac905f69c5c5.jpg").queue();
                return;
            }
            switch(check){
                case 0:
                    event.getChannel().sendMessage("Definitely not").queue();
                    break;
                case 1:
                    event.getChannel().sendMessage("Absolutely!").queue();
                    break;
                case 2:
                    event.getChannel().sendMessage("Have you tried shaking it?").queue();
                    break;
            }
        }else if(command.startsWith("!!baka")){
            event.getChannel().sendMessage("It's not like I like being a server mascot\nB-Baka!!!").queue();
            //event.getChannel().sendMessage("I-Its not like I LIKE being your server mascot, \nB-Baka").queue();
            event.getMessage().delete().queue();
        }
    }
}
