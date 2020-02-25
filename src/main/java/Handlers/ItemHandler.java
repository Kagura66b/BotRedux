package Handlers;

import Main.CSVImport;
import Main.Item;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ItemHandler {

    public static void getItem(String command, MessageReceivedEvent event, CSVImport dataTable){
        if(!command.contains(" ")){return;}
        command = command.split(" ", 2)[1];
        if(dataTable.getItem(command) == null){
            event.getChannel().sendMessage("Thats not an item, B-Baka").queue();
        } else {
            Item result = dataTable.getItem(command);
            event.getChannel().sendMessage(
                    "```***" + result.getName() + "***\n" +
                            "Cost: " + result.getPrice() + "\n" +
                            "Rarity: " + result.getRarity() + "\n" +
                            "Attunement: " + result.getAttunement() + "\n" +
                            "Description: " + result.getDescription() + "```"
            ).queue();
        }
    }
}
