package Main;

import me.xdrop.fuzzywuzzy.FuzzySearch;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVImport {
    ArrayList<String[]> records;


    public CSVImport(String path) {
        //Importing Item records from CSV
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
    }

    public int getSize(){
        return records.size();
    }

    public Item getItemByIndex(int index){
        String itemName = records.get(index)[0];
        if ( fuzzySearch(itemName, records) == null){
            return null;
        }
        Item result = fuzzySearch(itemName, records);
        return result;
    }

    public Item getItem(String input){
        Item output = fuzzySearch(input, records);
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


        String name = records.get(index)[0];
        int price = 0;
        if(!records.get(index)[4].equals("")) {
            try{
                price = Integer.parseInt(records.get(index)[4]);
            }catch (NumberFormatException ignored){
            }
        }
        String attunement;
        if(records.get(index)[2].contains("Yes")||records.get(index)[2].contains("No")){
            attunement = records.get(index)[2];
        }else if(records.get(index)[2].equals("")){
            attunement = "No";
        }else{
            attunement = "Yes";
        }
        String rarity = records.get(index)[1];
        if(rarity.equals("")){
            rarity = "None";
        }
        String description = records.get(index)[10];
        if(description.equals("")){
            description = "None";
        }
        boolean craftable;
        craftable = records.get(index)[12].contains("Yes");
        boolean consumable;
        consumable = records.get(index)[5].contains("Yes");
        boolean lootable;
        lootable = records.get(index)[13].contains("Yes");
        return new Item(name, index, price, attunement, rarity, description, craftable, consumable, lootable);
    }
}
