package me.trae.clans.fields.blocks.types;

import io.github.trae.di.annotations.type.component.Component;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.clans.fields.loot.Loot;
import me.trae.clans.fields.loot.impl.item.types.StaticQuantityItemLoot;

import java.util.List;

@Component
public class IronOreFieldsBlock implements FieldsBlock {

    @Override
    public List<String> getBlockIds() {
        return List.of("Ore_Iron_Basalt", "Ore_Iron_Sandstone", "Ore_Iron_Shale", "Ore_Iron_Slate", "Ore_Iron_Stone", "Ore_Iron_Volcanic");
    }

    @Override
    public List<Loot> getLootList() {
        return List.of(new StaticQuantityItemLoot("Ingredient_Bar_Iron", 1));
    }

    @Override
    public String getReplacementBlockId() {
        return "Rock_Bedrock";
    }
}