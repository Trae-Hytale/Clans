package me.trae.clans.fields.loot.impl.item.types;

import lombok.Getter;
import me.trae.clans.fields.loot.impl.item.ItemLoot;

@Getter
public class StaticQuantityItemLoot extends ItemLoot {

    private final int quantity;

    public StaticQuantityItemLoot(final int chance, final String itemId, final int quantity) {
        super(chance, itemId);

        this.quantity = quantity;
    }

    public StaticQuantityItemLoot(final String itemId, final int quantity) {
        this(100, itemId, quantity);
    }
}