package me.trae.clans.fields.blocks.types;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import io.github.trae.di.annotations.type.component.Component;
import lombok.AllArgsConstructor;
import me.trae.clans.economy.EconomyManager;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.clans.fields.loot.Loot;
import me.trae.clans.fields.loot.types.CoinsLoot;

import java.util.List;

@AllArgsConstructor
@Component
public class CoinsFieldsBlock implements FieldsBlock {

    private final EconomyManager economyManager;

    @Override
    public List<String> getBlockIds() {
        return List.of("Rock_Crystal_Yellow_Large", "Rock_Crystal_Yellow_Medium", "Rock_Crystal_Yellow_Small");
    }

    @Override
    public List<Loot> getLootList() {
        return List.of(new CoinsLoot(this.economyManager, blockId -> {
            return switch (blockId) {
                case "Rock_Crystal_Yellow_Large" -> 1000;
                case "Rock_Crystal_Yellow_Medium" -> 500;
                case "Rock_Crystal_Yellow_Small" -> 250;
                default -> throw new IllegalStateException("Unexpected value: %s".formatted(blockId));
            };
        }));
    }

    @Override
    public String getReplacementBlockId() {
        return BlockType.EMPTY.getId();
    }
}