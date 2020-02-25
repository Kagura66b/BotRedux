package Handlers;

import Main.CSVImport;
import Main.Item;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static java.time.temporal.ChronoUnit.SECONDS;

public class CraftHandler {

    CSVImport dataTable;
    List<String[]> craftBacklog;
    List<String[]> itemsDueToday;
    String fileName = "src/main/resources/items.csv";
    BufferedWriter writer;
    Random rand;
    ResponseHandler responseHandler;
    JDA jda;


    public CraftHandler(CSVImport importDataTable, ResponseHandler iResponseHandler, JDA importJDA) throws IOException {
        jda = importJDA
        ;responseHandler = iResponseHandler;
        dataTable = importDataTable;
        craftBacklog = importCraftingList();
        itemsDueToday = processBacklog(craftBacklog);
        writer = new BufferedWriter(new FileWriter(fileName, true));
        rand = new Random();
    }

    /*Craft Array Formatting
    0: Crafter
    1: StartDate
    2: EndDate
    3: Item
    4: guildID
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
                Member user = event.getGuild().getMember(event.getAuthor());
                boolean isAdmin = false;
                for(Role role : user.getRoles()) {
                    if (role.getName().contains("Mod")){
                        isAdmin = true;
                    }
                }
                if(isAdmin){
                    listall(event);
                }
                event.getMessage().delete().queue();
                break;
        }
    }

    private void listall(MessageReceivedEvent event){
        List<String[]> entries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null){
                String[] values  = line.split(",");
                entries.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> outputBuilder = new ArrayList<>();
        for (String[] entry : entries) {
            String name = event.getGuild().getMemberById(entry[0]).getNickname();
            entry[0] = name;
            entry[1] = entry[1].substring(0, 10);
            entry[2] = entry[2].substring(0, 10);
            outputBuilder.add(String.join(", ", entry));
        }
        String output = "" + String.join("\n", outputBuilder);
        event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> \n"
                + "The full Crafting Backlog is: \n"
                + "ID, StartDate, EndDate, Item \n"
                + output).queue();
    }

    private void showHelp(MessageReceivedEvent event){
        event.getChannel().sendMessage("***Crafting Commands:*** \n" +
                "```!!craft help: Shows help\n" +
                "!!craft list: Shows a list of your orders\n" +
                "!!craft new <Item>: Starts crafting a new Item\n" +
                "    Arguments:\n" +
                "    -intelligence|int|i # (REQUIRED. This is your raw stat, not your modifier)\n" +
                "    -proficiency # (use to set your proficiency. USE ONLY IF PROFICIENT)\n" +
                "    -expertise|expert|e (use if you have expertise)\n" +
                "    -tool|skill name of tool or skill (Use to set the tool or skill)```").queue();
    }

    private void generateCraftingList(MessageReceivedEvent event){
        String crafterID = event.getAuthor().getId();
        List<String[]> entries = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null){
                String[] values  = line.split(",");
                entries.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> outputBuilder = new ArrayList<>();
        for (String[] entry : entries) {
            if (entry[0].equals(crafterID)) {
                entry[0] = "<@" + entry[0] + ">";
                entry[1] = entry[1].substring(0, 9);
                entry[2] = entry[2].substring(0, 9);
                outputBuilder.add(String.join(", ", entry));
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
         */
        boolean[] delete = new boolean[parsing.length];
        delete[0] = true;
        delete[1] = true;
        String[] arguementArray = new String[4];
        arguementArray[0] = "false";
        arguementArray[1] = "0";
        arguementArray[2] = "0";
        arguementArray[3] = "None";
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
        System.out.println(item.isCraftable());
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
        event.getChannel().sendMessage("Base Time = "+baseTime+"\nBonus = "+totalBonus+"\nCrafting time = "+finalTime+" days").queue();
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
        String guildID = event.getGuild().getId();
        String channelID = event.getChannel().getId();

        String outputString = crafterID + "," + timestampStart + "," + timestampEnd + "," +  item.getName() + "," + guildID + "," + channelID;
        String[] outputArray = outputString.split(",");

        if(timeEnd.toLocalDate().equals(timeStart.toLocalDate())){
            itemsDueToday.add(outputArray);
        }
        craftBacklog.add(outputArray);

        try {
            writer.newLine();
            writer.write(outputString);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dateEnd = timeEnd.toLocalDate().toString();
        event.getChannel().sendMessage("Congratulations <@" + crafterID + ">! Your ***" + item.getName() + "*** will be ready on: " + dateEnd).queue();

    }

    private List<String[]> importCraftingList(){
        List<String[]> building = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null){
                String[] values  = line.split(",");
                building.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
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
        LocalDateTime target = LocalTime.parse("17:00").atDate(LocalDate.now());
        long trigger = LocalDateTime.now().until(target, SECONDS);
        if(trigger!=0){
            for (int i = 0; i < building.size(); i++) {
                String[] entry = building.get(i);
                TextChannel text = jda.getTextChannelById(entry[5]);
                text.sendMessage("<@" + entry[0] + ">\nYour ***" + entry[3] + "*** is ready for pickup").queueAfter(trigger, TimeUnit.SECONDS);
            }
        }
        return building;

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