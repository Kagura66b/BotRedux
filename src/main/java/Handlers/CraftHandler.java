package Handlers;

import GoogleHandlers.SheetInformationBuffer;
import Main.ItemDataTable;
import Main.Item;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.*;
import java.security.GeneralSecurityException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.SECONDS;

public class CraftHandler {

    ItemDataTable dataTable;
    List<String[]> craftBacklog;
    List<String[]> itemsDueToday;
    Random rand;
    ResponseHandler responseHandler;
    JDA jda;


    public CraftHandler(ItemDataTable importDataTable, ResponseHandler iResponseHandler, JDA importJDA) {
        jda = importJDA
        ;responseHandler = iResponseHandler;
        dataTable = importDataTable;
        craftBacklog = importCraftingList();
        itemsDueToday = processBacklog(craftBacklog);
        rand = new Random();
    }

    /*Craft Array Formatting
    0: Crafter
    1: StartDate
    2: EndDate
    3: Item
    4: CrafterChar
    5: channelID
     */

    public void processCommand(String command, MessageReceivedEvent event){
        command = command.toLowerCase();
        String[] parsing = command.split(" ");
        switch(parsing[1]){
            case "new":
                validateNewCraftEntry(parsing, event);
                break;
            case "list":
                generateCraftingList(event);
                break;
            case "help":
                showHelp(event);
                break;
            case "listall":
                listall(event);
                break;
            case "cancel":
                cancelCraft(event);
                break;
        }
    }

    private void cancelCraft(MessageReceivedEvent event){
        boolean isCrafting = false;
        int index = -1;
        for(String[] entry: craftBacklog){
            LocalDateTime end = LocalDateTime.parse(entry[2]);
            if(entry[0].contains(event.getAuthor().getId()) && LocalDateTime.now().isBefore(end)){
                isCrafting = true;
                index = craftBacklog.indexOf(entry);
            }
        }
        if(!isCrafting){
            event.getChannel().sendMessage("You are not crafting right now").queue();
            return;
        }

        long remaining = LocalDateTime.now().until(LocalDateTime.parse(craftBacklog.get(index)[2]), SECONDS);
        boolean isToday = remaining>0 && remaining<361;
        if(isToday){
            event.getChannel().sendMessage("It is too late to cancel this").queue();
            return;
        }

        String[] toBeDeleted = craftBacklog.get(index);
        craftBacklog.remove(index);

        List<Object> rowToRemove = new ArrayList<>();
        for(String field:toBeDeleted){
            rowToRemove.add(field);
        }

        try {
            SheetInformationBuffer.cancelCraft(rowToRemove);
        } catch (IOException e) {
            e.printStackTrace();
        }

        event.getChannel().sendMessage("<@" + toBeDeleted[0] + ">\nYour " + toBeDeleted[3] + " has been removed from the crafting queue").queue();

    }

    private void listall(MessageReceivedEvent event){
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
        int totalLength = 0;
        int index = 0;
        List<List<String>> outputBuilder = new ArrayList<>();
        outputBuilder.add(new ArrayList<>());
        for (String[] entry : craftBacklog) {
            int tempLength = 0;
            tempLength+=10;
            String[] entryToBuild = new String[4];
            String name = event.getGuild().getMemberById(entry[0]).getNickname();
            entryToBuild[0] = name;
            entryToBuild[1] = entry[1].substring(0, 10);
            entryToBuild[2] = entry[2].substring(0, 10);
            entryToBuild[3] = entry[3];
            for(String builtEntry:entryToBuild){
                tempLength+=builtEntry.length();
            }
            if(totalLength+tempLength>2000){
                index+=1;
                totalLength=tempLength;
            }else{
                totalLength+=tempLength;
                outputBuilder.add(new ArrayList<>());
            }
            outputBuilder.get(index).add(String.join(", ", entryToBuild));
        }
        String[] outputArray = new String[outputBuilder.size()];
        for(List<String> outputList:outputBuilder){
            outputArray[outputBuilder.indexOf(outputList)] = "" + String.join("\n", outputList);
        }
        event.getChannel().sendMessage("***<@" + event.getAuthor().getId() + "> \n"
                + "The full Crafting Backlog is: \n"
                + "ID, StartDate, EndDate, Item ***\n").queue();
        for(String output:outputArray){
            if(output!=null && !output.isEmpty()) {
                event.getChannel().sendMessage(output).queue();
            }
        }

    }

    private void showHelp(MessageReceivedEvent event){
        event.getChannel().sendMessage("***Crafting Commands:*** \n" +
                "```!!craft help: Shows help\n" +
                "!!craft list: Shows a list of your orders\n" +
                "!!craft new <character name> <Item>: Starts crafting a new Item\n" +
                "    Arguments:\n" +
                "    -intelligence|int|i # (REQUIRED. This is your raw stat, not your modifier)\n" +
                "    -proficiency # (use to set your proficiency. USE ONLY IF PROFICIENT)\n" +
                "    -expertise|expert|e (use if you have expertise)\n" +
                "    -tool|skill name of tool or skill (Use to set the tool or skill)```").queue();
    }

    private void generateCraftingList(MessageReceivedEvent event){
        String crafterID = event.getAuthor().getId();

        List<String> outputBuilder = new ArrayList<>();
        for (String[] entry : craftBacklog) {
            if (entry[0].equals(crafterID)) {
                String[] entryToBuild = new String[4];
                String name = event.getGuild().getMemberById(entry[0]).getNickname();
                entryToBuild[0] = name;
                entryToBuild[1] = entry[1].substring(0, 10);
                entryToBuild[2] = entry[2].substring(0, 10);
                entryToBuild[3] = entry[3];
                outputBuilder.add(String.join(", ", entryToBuild));
            }
        }
        String output = "" + String.join("\n", outputBuilder);
        event.getChannel().sendMessage("<@" + crafterID + "> \n"
                + "Your Crafting backlog is: \n"
                + "ID, StartDate, EndDate, Item \n"
                + output).queue();
    }


    //parse arguments<>
    //determine end date <>
    //correct end time if it is after the end of the daily cycle
    //reform item name after removing parsed arguments
    //generate the string to write to file
    //generate the array to write to the backlog
    //if the crafting cycle isnt over write the array to the list for today's processing
    //write the array to the backlog
    //write the entry to file
    //announce completion
    private void validateNewCraftEntry(String[] parsing, MessageReceivedEvent event) {

        //<editor-fold desc="Arguement Deconstruction">
        /* Argument layout
        0 - expertise bool
        1 - prof int
        2 - intel int
        3 - tool/skill string
        4 - crafterChar
         */
        boolean[] delete = new boolean[parsing.length];
        delete[0] = true;
        delete[1] = true;
        delete[2] = true;
        String[] arguementArray = new String[5];
        arguementArray[0] = "false";
        arguementArray[1] = "0";
        arguementArray[2] = "0";
        arguementArray[3] = "None";
        arguementArray[4] = parsing[2];
        for (int i = 2; i < parsing.length; i++) {
            if (parsing[i].equals("-expertise") || parsing[i].equals("-expert") || parsing[i].equals("-e")) {
                arguementArray[0] = "true";
                delete[i] = true;
            } else if (parsing[i].contains("-proficiency")||parsing[i].contains("-prof")||parsing[i].contains("-p")) {
                arguementArray[1] = parsing[i + 1];
                delete[i] = true;
                delete[i + 1] = true;
            } else if (parsing[i].contains("-intelligence") || parsing[i].contains("-int") || parsing[i].equals("-i")) {
                arguementArray[2] = parsing[i + 1];
                delete[i] = true;
                delete[i + 1] = true;
            } else if (parsing[i].contains("-tool") || parsing[i].contains("-skill")) {
                arguementArray[3] = parsing[i + 1];
                delete[i] = true;
                delete[i + 1] = true;
            }
        }
        List<String> nameGen = new ArrayList<>();
        for (int i = 0; i < parsing.length; i++) {
            if (!delete[i]) {
                nameGen.add(parsing[i]);
            }
        }
        String itemString = "";
        for (String build : nameGen) {
            itemString = itemString.concat(build + " ");
        }

        //</editor-fold>

        //Retrieve item name
        Item item = dataTable.getItem(itemString);
        if (item == null) {
            event.getChannel().sendMessage("Thats not an item, B-Baka").queue();
            return;
        }else if(!item.isCraftable()){
            event.getChannel().sendMessage("Nice try, you can't craft that!!!").queue();
            return;
        }

        //finalize the argstring, itemstring, and event
        final MessageReceivedEvent finalEvent = event;
        final String[] finalArgArray = arguementArray;
        final Item finalItem = item;

        //validate user does not have another job in progress
        boolean isCrafting = false;
        for(String[] entry: craftBacklog){
            LocalDateTime end = LocalDateTime.parse(entry[2]);
            if(entry[0].contains(event.getAuthor().getId()) && LocalDateTime.now().isBefore(end) && entry[4].contains(arguementArray[4])){
                isCrafting = true;
            }
        }

        if(isCrafting){
            event.getChannel().sendMessage(arguementArray[4] + " is already crafting").queue();
            return;
        }

        //<editor-fold desc="getCraftingTime">
        int totalBonus = 10+((Integer.parseInt(arguementArray[2])-10)/2);
        System.out.println((Integer.parseInt(arguementArray[2])-10)/2 + " and " + totalBonus);
        if(arguementArray[1] != null) {
            if (arguementArray[0].equals("true")) {
                totalBonus += (Integer.parseInt(arguementArray[1]) * 2);
            } else {
                totalBonus += Integer.parseInt(arguementArray[1]);
            }
        }
        int baseTime=20;
        String rarity = item.getRarity();
        switch(rarity){
            case "common":
            case "Common":
                baseTime = 20;
                if(item.isConsumable()){
                    baseTime = baseTime/3;
                }
                break;
            case "uncommon":
            case "Uncommon":
                baseTime = 100;
                if(item.isConsumable()){
                    baseTime = baseTime/3;
                }
                break;
            case "rare":
            case "Rare":
                baseTime = 240;
                if(item.isConsumable()){
                    baseTime = baseTime/4;
                }
                break;
            case "very rare":
            case "Very Rare":
                baseTime = 400;
                if(item.isConsumable()){
                    baseTime = baseTime/4;
                }
                break;
            case "legendary":
            case "Legendary":
                baseTime = 500;
                if(item.isConsumable()){
                    baseTime = baseTime/4;
                }
                break;
        }
        int finalTime = baseTime/totalBonus;
        event.getChannel().sendMessage("Base Time for " + arguementArray[4] +  " is = "+baseTime+"\nBonus = "+totalBonus+"\nCrafting time "+finalTime+" days").queue();
        //</editor-fold>


        event.getChannel().sendMessage("<@" + event.getAuthor().getId() + ">\nAre you sure you want to craft:\n***" + item.getName() + "***?\nReact with :white_check_mark: or  :negative_squared_cross_mark:")
                .queue(new Consumer<Message>() {
                    @Override
                    public void accept(Message message) {
                        message.addReaction("\u2705").queue();
                        message.addReaction("\u274E").queue();
                        String mID = message.getId();
                        String author = finalEvent.getAuthor().getId();
                        String validate = responseHandler.waitForReaction(mID, author);
                        if(validate == null){
                            message.delete().queue();
                            message.getChannel().sendMessage("Timed Out").queue();
                        }else if(validate.contains("no")){
                            message.delete().queue();
                            message.getChannel().sendMessage("Cancelled by user").queue();
                        }else{
                            //message.getChannel().sendMessage("Working as intended").queue();
                            createNewCraftEntry(finalEvent, finalArgArray, finalItem);
                            message.delete().queue();
                        }
                    }
                });
    }

    public void createNewCraftEntry(MessageReceivedEvent event, String[] args, Item item){
        /* Argument layout
        0 - expertise bool
        1 - prof int
        2 - intel int
        3 - tool/skill string
        4 - CrafterChar
         */
        int totalBonus = 10+((Integer.parseInt(args[2])-10)/2);
        System.out.println((Integer.parseInt(args[2])-10)/2 + " and " + totalBonus);
        if(args[1] != null) {
            if (args[0].equals("true")) {
                totalBonus += (Integer.parseInt(args[1]) * 2);
            } else {
                totalBonus += Integer.parseInt(args[1]);
            }
        }
        int baseTime=20;
        String rarity = item.getRarity();
        switch(rarity){
            case "common":
            case "Common":
                baseTime = 20;
                if(item.isConsumable()){
                    baseTime = baseTime/3;
                }
                break;
            case "uncommon":
            case "Uncommon":
                baseTime = 100;
                if(item.isConsumable()){
                    baseTime = baseTime/3;
                }
                break;
            case "rare":
            case "Rare":
                baseTime = 240;
                if(item.isConsumable()){
                    baseTime = baseTime/4;
                }
                break;
            case "very rare":
            case "Very Rare":
                baseTime = 400;
                if(item.isConsumable()){
                    baseTime = baseTime/4;
                }
                break;
            case "legendary":
            case "Legendary":
                baseTime = 500;
                if(item.isConsumable()){
                    baseTime = baseTime/4;
                }
                break;
        }
        int finalTime = baseTime/totalBonus;
        LocalDateTime timeStart = LocalDateTime.now();
        LocalDateTime timeEnd = LocalDateTime.now().plusDays(finalTime);
        String crafterID = event.getAuthor().getId();
        //Corrects time if it is after the completion of the daily crafting cycle
        if(timeEnd.toLocalDate().equals(timeStart.toLocalDate())){
            if(timeStart.toLocalTime().isAfter(LocalTime.of(13,0))){
                timeEnd = timeEnd.plusDays(1);
            }
        }
        String timestampStart = timeStart.toString();
        String timestampEnd = timeEnd.toString();
        String crafterChar = args[4];
        String channelID = event.getChannel().getId();

        String outputString = crafterID + "," + timestampStart + "," + timestampEnd + "," +  item.getName() + "," + crafterChar + "," + channelID;
        String[] outputArray = outputString.split(",");

        if(timeEnd.toLocalDate().equals(timeStart.toLocalDate())){
            itemsDueToday.add(outputArray);
        }
        craftBacklog.add(outputArray);

        List<Object> writeList = new ArrayList<>();
        for(String field:outputArray){
            writeList.add(field);
        }

        try {
            SheetInformationBuffer.writeToCraft(writeList);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dateEnd = timeEnd.toLocalDate().toString();
        event.getChannel().sendMessage("Congratulations <@" + crafterID + ">! Your ***" + item.getName() + "*** will be ready on: " + dateEnd).queue();

    }

    private List<String[]> importCraftingList(){
        List<String[]> building = new ArrayList<>();
        List<List<String>> rawData = new ArrayList<>();
        try {
            rawData = SheetInformationBuffer.retrieveCraftSheetData();
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }

        for(List<String> row:rawData){
            String[] rowArray = new String[row.size()];
            for(int i = 0; i<row.size(); i++){
                rowArray[i] = row.get(i);
            }
            building.add(rowArray);
        }

        return building;
    }

    private List<String[]> processBacklog(List<String[]> backlog){
        List<String[]> building = new ArrayList<>();
        for(String[] entry : backlog){
            if(!entry[0].equals("")) {
                LocalDate today = LocalDateTime.now().toLocalDate();
                LocalDate finish = LocalDateTime.parse(entry[2]).toLocalDate();
                if (today.equals(finish)) {

                    building.add(entry);
                }
            }
        }
        /*LocalDateTime target = LocalTime.parse("13:00").atDate(LocalDate.now());
        long trigger = LocalDateTime.now().until(target, SECONDS);
        if(trigger!=0){
            for (int i = 0; i < building.size(); i++) {
                String[] entry = building.get(i);
                TextChannel text = jda.getTextChannelById(entry[5]);
                assert text != null;
                //text.sendMessage("<@" + entry[0] + ">\nYour ***" + entry[3] + "*** is ready for pickup").queueAfter(trigger, TimeUnit.SECONDS);
            }
        }*/
        return building;

    }

    public List<String[]> getCraftBacklog(){
        return craftBacklog;
    }
}
 /*Craft Array Formatting
    0: Crafter
    1: StartDate
    2: EndDate
    3: Item
    4: guildID
    5: channelID
 */