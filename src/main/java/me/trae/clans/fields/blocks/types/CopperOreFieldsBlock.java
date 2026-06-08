package me.trae.clans.fields.blocks.types;

import io.github.trae.di.annotations.type.component.Component;
import lombok.AllArgsConstructor;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.clans.fields.loot.Loot;
import me.trae.clans.fields.loot.impl.item.types.StaticQuantityItemLoot;

import java.util.List;

@AllArgsConstructor
@Component
public class CopperOreFieldsBlock implements FieldsBlock {

    @Override
    public List<String> getBlockIds() {
        return List.of("Ore_Copper_Stone", "Ore_Copper_Shale", "Ore_Copper_Sandstone");
    }

    @Override
    public List<Loot> getLootList() {
        return List.of(new StaticQuantityItemLoot("Ingredient_Bar_Copper", 1));
    }

    @Override
    public String getReplacementBlockId() {
        return "Rock_Bedrock";
    }
}