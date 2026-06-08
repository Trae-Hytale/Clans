package me.trae.clans.fields.blocks.types;

import io.github.trae.di.annotations.type.component.Component;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.clans.fields.loot.Loot;
import me.trae.clans.fields.loot.impl.item.types.DynamicQuantityItemLoot;
import me.trae.clans.fields.loot.impl.item.types.StaticQuantityItemLoot;

import java.util.List;

@Component
public class TreasureChestFieldsBlock implements FieldsBlock {

    @Override
    public List<String> getBlockIds() {
        return List.of("Furniture_Dungeon_Chest_Epic");
    }

    @Override
    public List<Loot> getLootList() {
        return List.of(
                new DynamicQuantityItemLoot(100, "Ingredient_Bar_Copper", 1, 8),
                new DynamicQuantityItemLoot(100, "Ingredient_Bar_Iron", 1, 8),
                new DynamicQuantityItemLoot(100, "Ingredient_Bar_Gold", 1, 8),
                new DynamicQuantityItemLoot(80, "Ingredient_Bar_Thorium", 1, 6),
                new DynamicQuantityItemLoot(80, "Ingredient_Bar_Cobalt", 1, 6),
                new DynamicQuantityItemLoot(60, "Ingredient_Bar_Silver", 1, 4),
                new DynamicQuantityItemLoot(60, "Ingredient_Bar_Adamantite", 1, 4),
                new DynamicQuantityItemLoot(40, "Ingredient_Bar_Mithril", 1, 2),

                new StaticQuantityItemLoot(10, "Weapon_Sword_Mithril", 1),
                new StaticQuantityItemLoot(20, "Weapon_Shortbow_Mithril", 1),
                new StaticQuantityItemLoot(30, "Tool_Pickaxe_Mithril", 1),
                new StaticQuantityItemLoot(40, "Tool_Hatchet_Mithril", 1)
        );
    }

    @Override
    public String getReplacementBlockId() {
        return "Furniture_Ancient_Chest_Small";
    }
}