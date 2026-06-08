package me.trae.clans.fields.blocks.types;

import io.github.trae.di.annotations.type.component.Component;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.clans.fields.loot.Loot;
import me.trae.clans.fields.loot.impl.item.types.StaticQuantityItemLoot;

import java.util.List;

@Component
public class MithrilOreFieldsBlock implements FieldsBlock {

    @Override
    public List<String> getBlockIds() {
        return List.of("Ore_Mithril_Stone");
    }

    @Override
    public List<Loot> getLootList() {
        return List.of(new StaticQuantityItemLoot("Ingredient_Bar_Mithril", 1));
    }

    @Override
    public String getReplacementBlockId() {
        return "Rock_Bedrock";
    }
}