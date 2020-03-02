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
            case "property":
            case "prop":
            case "minor":
                getMinorProperty(event);
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
                "!!loot help: Shows this help list" +
                "!!loot property|prop|minor: generate a minor property```";
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
        return (int) Math.floor(number*(0.717*Math.pow(cr,3.25)+8));
    }

    private void getMinorProperty(MessageReceivedEvent event){
        int property = new Random().nextInt(20);
        String out = "It fucked up";
        switch(property){
            case 0:
                out="***Artful***\n" +
                        "The bearer is proficient in one tool or set of tools, of the GM’s choice, while the item is on the bearer’s person.";
                break;
            case 1:
                out = "***Beackoner***\n" +
                        "If the item is separated from the bearer, the bearer instinctively knows the item’s direction and rough distance. This knowledge lasts for 24 hours or until the item is attuned to someone else, whichever comes first.";
                break;
            case 2:
                out = "***Brave***\n" +
                        "When the bearer of this item contemplates or undertakes a cowardly act—or even a neutral act, when a more daring option is available—the item enhances feelings of shame and guilt.";
                break;
            case 3:
                out = "***Bulwark***\n" +
                        "If the item is in the bearer’s possession for the entirety of a long rest, the item grants the bearer 5 temporary hit points at the conclusion of that rest. If not used, these hit points fade in 24 hours.\n";
                break;
            case 4:
                out = "***Cowardly***\n" +
                        "When the bearer is presented with an opportunity to act in a cowardly or self-preserving manner, the item heightens the bearer’s urge to do so.\n";
                break;
            case 5:
                out = "*** Devoted ***\n" +
                        "The item fights to remain attuned to its current bearer. The item must be separated from the bearer for 48 hours, rather than the standard 24, to break the attunement. If a new individual attempts to attune the item when a prior attunement is still in effect, the individual must succeed on a DC 14 Charisma check at the end of that short rest. On a failure, the item fails to attune to the new owner, remaining attuned to the old.\n";
                break;
            case 6:
                out = "***Enigmatic***\n" +
                        "Neither examination over the course of a short rest, nor identify or similar magics, reveal this item’s properties. The bearer can learn those properties only through attunement or trial and error.\n";
                break;
            case 7:
                out = "***Fastidious***\n" +
                        "Once per day, as an action, the bearer can magically clean themselves, their outfit, and the item itself of dirt, grime, sweat, and so forth. This power resets at dawn.\n";
                break;
            case 8:
                out = "***Focused***\n" +
                        "The bearer of this item gains a +1 bonus to Constitution saving throws made to avoid losing concentration on an ongoing spell.\n";
                break;
            case 9:
                out = "***Gloom-Sight***\n" +
                        "If the bearer of this item possesses darkvision, the effective distance of that darkvision increases by 20 feet while the item is on the bearer’s person. It does not, however, grant darkvision to a bearer who does not already possess it.\n";
                break;
            case 10:
                out = "***Hardy***\n" +
                        "If the item is in the bearer’s possession for the entirety of a long rest, then at the conclusion of that rest, the item grants the bearer one extra hit die, which can be spent as normal to regain hit points during a short rest. If not used, this extra hit die disappears in 24 hours.\n";
                break;
            case 11:
                out = "***Lodestar***\n" +
                        "This item is linked to a specific location or object, determined by the GM. The bearer can use an action to determine the distance and direction to that place or object. The item does not grant the bearer any specific knowledge of what that item or place might be.\n";
                break;
            case 12:
                out = "***Restful***\n" +
                        "If the item is in the bearer’s possession since the beginning of a long rest, the bearer requires only six hours to gain the benefit of that rest, rather than the standard eight. The bearer can still gain the benefits of a long rest only once per day.\n";
                break;
            case 13:
                out = "***Resucitator***\n" +
                        "If the item is in the bearer’s possession, the bearer gains a +2 bonus to the first death save made that day. This power resets at dawn.\n";
                break;
            case 14:
                out = "***Schemer***\n" +
                        "As an action while the item is on the bearer’s person, the bearer may choose one individual within both sight and earshot. Until the bearer ends the effect (no action required) or a full minute passes, anything the bearer says is heard only by that individual, not by anyone else. (This is a one-way effect only.) This power can be used once a day and resets at dawn.";
                break;
            case 15:
                out = "***Skillful***\n" +
                        "The bearer gains a +1 bonus to ability checks using one specific skill, of the GM’s choice, while the item is on the bearer’s person.\n";
                break;
            case 16:
                out = "***Speedy***\n" +
                        "As an action while the item is on the bearer’s person, the bearer may gain a +5 to walking speed. This bonus lasts until the bearer deactivates it (no action required). The item grants this bonus up to a maximum total duration of one minute per day, resetting at dawn.\n";
                break;
            case 17:
                out = "***Suppressible***\n" +
                        "As an action, the bearer may suppress the item’s magic, in its entirety, for a preset amount of time (up to 24 hours). During that time, the item behaves as a non-magical item, cannot be attuned by anyone (though existing attunement does not end), and does not register as magical to detect magic, identify, or any other spells or abilities that detect magic. The effect cannot be ended prematurely; it lasts for the duration specified when activated.\n";
                break;
            case 18:
                out = "***Unwavering***\n" +
                        "The item grants the bearer an extra sliver of luck to slightly mitigate unfavorable conditions. When the item is created, the GM chooses one of the following: attack rolls, saving throws, or ability checks. When the bearer makes a roll of the chosen type with disadvantage, the item grants a +1 bonus on that roll.\n";
                break;
            case 19:
                out = "***Dual-Featured***\n" +
                        "Roll twice, rerolling any additional 20s.\n";
                break;
        }
        event.getChannel().sendMessage(out).queue();
    }

}
