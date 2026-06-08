package me.trae.clans.fields.loot.impl.item.types;

import io.github.trae.utilities.UtilNumber;
import lombok.Getter;
import me.trae.clans.fields.loot.impl.item.ItemLoot;

@Getter
public class DynamicQuantityItemLoot extends ItemLoot {

    private final int minimumQuantity, maximumQuantity;

    public DynamicQuantityItemLoot(final int chance, final String itemId, final int minimumQuantity, final int maximumQuantity) {
        super(chance, itemId);

        this.minimumQuantity = minimumQuantity;
        this.maximumQuantity = maximumQuantity;
    }

    public DynamicQuantityItemLoot(final String itemId, final int minimumQuantity, final int maximumQuantity) {
        this(100, itemId, minimumQuantity, maximumQuantity);
    }

    @Override
    public int getQuantity() {
        return UtilNumber.getRandomNumber(Integer.class, this.getMinimumQuantity(), this.getMaximumQuantity());
    }
}