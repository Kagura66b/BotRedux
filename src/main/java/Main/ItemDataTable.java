package Main;

import GoogleHandlers.SheetInformationBuffer;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class ItemDataTable {
    //ArrayList<String[]> records;
    ArrayList<String[]> newRecords;
    final String sheetID = "12Y06S_7WHCj6gYXVZLcph0PSpe2sdiudErWamr7z0Ck";
    final String range = "Bot Friendly";


    public ItemDataTable(String path) {
        //Importing Item records from CSV
        List<List<String>> rawData = new ArrayList<>();
        try {
            rawData = SheetInformationBuffer.retrieveItemSheetData();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        newRecords = new ArrayList<>();
        for(List<String> row:rawData) {
            String[] rowArray = new String[row.size()];
            for (int i = 0; i < row.size(); i++) {
                rowArray[i] = row.get(i);
            }
            // (String[])row.toArray();
            newRecords.add(rowArray);
        }
        for(String[] entry: newRecords){
            if(entry.length!=15){
                System.out.println("ALERT ON LINE: " + newRecords.indexOf(entry));
            }else{
                System.out.print("1 ");
            }
        }
        System.out.println("\n" + newRecords.size() + " records scanned");
        /*
        records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null){
                String[] values  = line.split(",");
                if(values.length!=15){System.out.println("ALERT");}
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].replaceAll("a446", "\n");
                    values[i] = values[i].replaceAll("a445", ",");
                }
                records.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }

    public void rebuildDataTable(MessageReceivedEvent event){
        List<List<String>> rawData = new ArrayList<>();
        try {
            rawData = SheetInformationBuffer.retrieveItemSheetData();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        newRecords = new ArrayList<>();
        for(List<String> row:rawData) {
            String[] rowArray = new String[row.size()];
            for (int i = 0; i < row.size(); i++) {
                rowArray[i] = row.get(i);
            }
            // (String[])row.toArray();
            newRecords.add(rowArray);
        }
        for(String[] entry: newRecords){
            if(entry.length!=15){
                System.out.println("ALERT ON LINE: " + newRecords.indexOf(entry));
            }
        }
        event.getChannel().sendMessage(newRecords.size() + " Entries processed").queue();
    }

    public int getSize(){
        return newRecords.size();
    }

    public Item getItemByIndex(int index){
        String itemName = newRecords.get(index)[0];
        if ( fuzzySearch(itemName, newRecords) == null){
            return null;
        }
        Item result = fuzzySearch(itemName, newRecords);
        return result;
    }

    public Item getItem(String input){
        Item output = fuzzySearch(input, newRecords);
        return output;
    }

    private Item fuzzySearch(String in, List<String[]> list){

        int index = -1;
        int best = 0;
        for (int i = 0; i < list.size(); i++) {
            int compare = FuzzySearch.ratio(in, list.get(i)[0]);
            if (compare > best) {
                index = i;
                best = compare;
            }
        }

        if (best<50 || index == -1){
            return null;
        }


        /*Data Structure for the records File
        0: Name
        1: Rarity
        2: Attunement
        3: Type
        4: Cost
        5: Consumable
        6: Tools
        7: Spells Needed
        8: Trophy
        9: MISC requirements
        10: Description
        11: Source
        12: Craftable
        13: Lootable
         */


        String name = list.get(index)[0];
        int price = 0;
        if(!list.get(index)[4].equals("")) {
            try{
                price = Integer.parseInt(list.get(index)[4]);
            }catch (NumberFormatException ignored){
            }
        }
        String attunement;
        if(list.get(index)[2].contains("Yes")||list.get(index)[2].contains("No")){
            attunement = list.get(index)[2];
        }else if(list.get(index)[2].equals("")){
            attunement = "No";
        }else{
            attunement = "Yes";
        }
        String rarity = list.get(index)[1];
        if(rarity.equals("")){
            rarity = "None";
        }
        String description = list.get(index)[10];
        if(description.equals("")){
            description = "None";
        }
        boolean craftable;
        craftable = list.get(index)[12].contains("Yes");
        boolean consumable;
        consumable = list.get(index)[5].contains("Yes");
        boolean lootable;
        lootable = list.get(index)[13].contains("Yes");
        return new Item(name, index, price, attunement, rarity, description, craftable, consumable, lootable);
    }
}
