package Main;

public class Item {
    String name;
    int index;
    int price;
    boolean craftable;
    boolean consumable;
    boolean lootable;
    String attunement;
    String rarity;
    String description;

    public Item(String initName, int initIndex, int initPrice, String initAttune, String initRare, String initDesc, boolean initCraftable, boolean initConsumable, boolean initLootable){
        name=initName;
        index=initIndex;
        price=initPrice;
        attunement=initAttune;
        rarity=initRare;
        description=initDesc;
        craftable=initCraftable;
        consumable=initConsumable;
        lootable=initLootable;
    }

    public String getName() {
        return name;
    }

    public int getIndex() {
        return index;
    }

    public String getAttunement() {
        return attunement;
    }

    public int getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getRarity() {
        return rarity;
    }

    public boolean isCraftable() {
        return craftable;
    }

    public boolean isConsumable() {
        return consumable;
    }

    public boolean isLootable() {
        return lootable;
    }
}
