package Handlers;

import Main.ItemDataTable;
import Main.Item;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("IntegerDivisionInFloatingPointContext")
public class LootHandler {

    ItemDataTable datatable;

    public LootHandler(ItemDataTable importTable){
        datatable = importTable;
    }

    public void processCommand(String command, MessageReceivedEvent event){
        command = command.toLowerCase();
        String[] parsing = command.split(" ");
        switch(parsing[1]){
            case "gold":
                getLootGold(parsing, event);
                break;
            case "items":
                getLootItems(parsing, event);
                break;
            case "help":
                showHelp(event);
                break;
        }
    }

    public void showHelp(MessageReceivedEvent event){
        String out = "```!!loot gold <List of CRs>: Generates loot for an encounter. \n" +
                "    The arguements for this are cr1 # cr2 # etc. \n" +
                "    An example would be   \"!!loot gold cr0.25 8 cr4 4 cr5 1\" \n    for an encounter containing 1 cr5, 4 cr4s, and 8 cr1/4s\n" +
                "    low CRs can be shown as either 1/4 or 0.25\n" +
                "    From this point magic items can be generated using the following command\n" +
                "!!loot items <amount of money>: generates a loot item list based on the amount of gold it is given\n" +
                "!!loot help: Shows this help list```";
        event.getChannel().sendMessage(out).queue();

    }


    private void getLootGold(String[] parsing, MessageReceivedEvent event){
        int[] monsters = new int[22];
        for(int i=0; i<22; i++){
            monsters[i]=0;
        }
        //convert parsing into CR based monster counts
        //0=CR0.25, 21=CR0.5, all else i=CR
        System.out.println();
        for (int i = 2; i < parsing.length; i++) {
            if (parsing[i].contains("cr1/4") || parsing[i].contains("cr0.25") || parsing[i].contains("cr1/8") || parsing[i].contains("cr0.125")) {
                monsters[0] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr1/2") || parsing[i].contains("cr0.5")) {
                monsters[21] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr3")){
                monsters[3] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr4")){
                monsters[4] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr5")){
                monsters[5] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr6")){
                monsters[6] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr7")){
                monsters[7] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr8")){
                monsters[8] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr9")){
                monsters[9] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr10")){
                monsters[10] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr11")){
                monsters[11] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr12")){
                monsters[12] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr13")){
                monsters[13] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr14")){
                monsters[14] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr15")){
                monsters[15] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr16")){
                monsters[16] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr17")){
                monsters[17] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr18")){
                monsters[18] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr19")){
                monsters[19] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr20")){
                monsters[20] = Integer.parseInt(parsing[i+1]);
            } else if (parsing[i].contains("cr1")){
                monsters[1] = Integer.parseInt(parsing[i+1]);
            }else if (parsing[i].contains("cr2")){
                monsters[2] = Integer.parseInt(parsing[i+1]);
            }
        }

        double totalCount = 0;
        for(int counter: monsters){
            totalCount+=counter;
        }
        int goldTotal = 0;
        double grossCount = 0;
        for (int i=0; i<22; i++) {
            if (i == 0) {
                int add = (int) Math.floor(monsters[i] * 0.25);
                goldTotal += getTreasure(0.25, monsters[i]);
                grossCount += add;
            } else if (i == 21) {
                int add = (int) Math.floor(monsters[i] * 0.5);
                goldTotal += getTreasure(0.5, monsters[i]);
                grossCount += add;
            } else {
                int add = monsters[i] * i;
                goldTotal += getTreasure(i, monsters[i]);
                grossCount += add;
            }
        }
        double average = (grossCount/totalCount);
        System.out.println(grossCount + "/" + totalCount + "=" + average);
        event.getChannel().sendMessage("The average CR was: "+ average + "\nThe encounter gold total is: " + goldTotal).queue();
    }

    private void getLootItems(String[] parsing, MessageReceivedEvent event){
        int gold = Integer.parseInt(parsing[2]);
        List<Item> treasure = new ArrayList<>();
        int bounds = datatable.getSize();
        Random rand = new Random();
        while(gold>10){
            int index = rand.nextInt(bounds);
            if(datatable.getItemByIndex(index) != null){
                Item pending = datatable.getItemByIndex(index);
                if(pending.getPrice()<=gold && pending.getPrice() != 0 && pending.isLootable()) {
                    treasure.add(pending);
                    gold -= pending.getPrice();
                }
            }
        }
        StringBuilder output = new StringBuilder("```Treasure from " + parsing[2] + " gold:");
        for(Item parse:treasure){
            output.append("\n").append(parse.getName()).append(": ").append(parse.getPrice()).append(" gold");
        }
        output.append("\n\nDon't forget to subtract the gold from the loot total```");
        event.getChannel().sendMessage(output.toString()).queue();
    }

    private int getTreasure(double cr, int number){
        return (int) Math.floor(number*(0.717*Math.pow(cr,3.311)+4));
    }

}
