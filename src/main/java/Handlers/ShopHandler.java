package Handlers;

import GoogleHandlers.SheetInformationBuffer;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class ShopHandler {
    public static final String shopID = "1U4ilMqpE7rINHN3P5EEUmXr_f0hZY-geWLuPReTRWNg";
    public static final String messageID = "699671498763272373";

    public ShopHandler(){

    }

    public void processCommand(String command, MessageReceivedEvent event){
        String[] args = command.split(" ");
        if((args[1].equals("buy") || args[1].equals("Buy")) && args.length>=3){
            buyItem(args, event);
        }
    }

    private void buyItem(String[] args, MessageReceivedEvent event){
        int index = Integer.valueOf(args[2])-1;
        List<List<Object>> inventory = getCurrentShop();

        if(inventory.get(index).size() == 4){
            return;
        }

        List<List<Object>> newInventory = new ArrayList<>();
        for(int i = 0; i<inventory.size(); i++){
            List<Object> row;
            if(i==index){
                row = inventory.get(i);
                row.add(event.getAuthor().getName());
            }else{
                row = inventory.get(i);
            }
            newInventory.add(row);
        }

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i<newInventory.size()-1; i++){
            List<Object> row = newInventory.get(i);
            builder.append(i+1 + ": " + row.get(0) + " for " + row.get(2) + " gold");
            if(row.size()>3){
                builder.append(" purchased by " + row.get(3));
            }
            if(i != newInventory.size()-2) {
                builder.append("\n");
            }
        }
        event.getChannel().editMessageById(messageID, builder.toString()).queue();
        setCurrentShop(newInventory);
    }

    private List<List<Object>> getCurrentShop(){
        String range = "HenryCurrent";
        List<List<Object>> result =  new ArrayList<>();
        try {
            result = SheetInformationBuffer.readFromSheet(shopID, range);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return result;
    }

    private void setCurrentShop(List<List<Object>> body){
        String range = "HenryCurrent";
        try {
            SheetInformationBuffer.writeToSheet(shopID, range, body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
