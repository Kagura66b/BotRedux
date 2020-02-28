package Handlers;

import Main.ItemDataTable;
import Main.Item;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ItemHandler {


    public static void processCommand(String command, MessageReceivedEvent event, ItemDataTable dataTable){
        command = command.toLowerCase();
        String[] parsing = command.split(" ");
        if(parsing[1].contains("rebuild")){
            Member user = event.getGuild().getMember(event.getAuthor());
            boolean isAdmin = false;
            for(Role role : user.getRoles()) {
                if (role.getName().contains("Mod")){
                    isAdmin = true;
                }
            }
            event.getMessage().delete().queue();
            if(!isAdmin){
                return;
            }
            dataTable.rebuildDataTable(event);
            event.getChannel().sendMessage("DataTable has been rebuilt").queue();
        }else{
            getItem(command, event, dataTable);
        }
    }

    public static void getItem(String command, MessageReceivedEvent event, ItemDataTable dataTable){
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
