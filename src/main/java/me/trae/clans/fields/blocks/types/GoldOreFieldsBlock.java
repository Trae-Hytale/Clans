package me.trae.clans.fields.blocks.types;

import io.github.trae.di.annotations.type.component.Component;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.clans.fields.loot.Loot;
import me.trae.clans.fields.loot.impl.item.types.StaticQuantityItemLoot;

import java.util.List;

@Component
public class GoldOreFieldsBlock implements FieldsBlock {

    @Override
    public List<String> getBlockIds() {
        return List.of("Ore_Gold_Basalt", "Ore_Gold_Calcite", "Ore_Gold_Sandstone", "Ore_Gold_Shale", "Ore_Gold_Stone", "Ore_Gold_Volcanic");
    }

    @Override
    public List<Loot> getLootList() {
        return List.of(new StaticQuantityItemLoot("Ingredient_Bar_Gold", 1));
    }

    @Override
    public String getReplacementBlockId() {
        return "Rock_Bedrock";
    }
}