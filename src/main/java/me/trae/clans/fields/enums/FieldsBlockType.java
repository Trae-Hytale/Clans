package me.trae.clans.fields.enums;

import io.github.trae.utilities.UtilString;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum FieldsBlockType {

    COPPER_ORE(List.of("Ore_Copper_Stone", "Ore_Copper_Shale", "Ore_Copper_Sandstone")),
    IRON_ORE(List.of("Ore_Iron_Basalt", "Ore_Iron_Sandstone", "Ore_Iron_Shale", "Ore_Iron_Slate", "Ore_Iron_Stone", "Ore_Iron_Volcanic")),
    GOLD_ORE(List.of("Ore_Gold_Basalt", "Ore_Gold_Calcite", "Ore_Gold_Sandstone", "Ore_Gold_Shale", "Ore_Gold_Stone", "Ore_Gold_Volcanic")),
    THORIUM_ORE(List.of("Ore_Thorium_Sandstone", "Ore_Thorium_Mud")),
    COBALT_ORE(List.of("Ore_Cobalt_Shale", "Ore_Cobalt_Slate")),
    SILVER_ORE(List.of("Ore_Silver_Basalt", "Ore_Silver_Sandstone", "Ore_Silver_Shale", "Ore_Silver_Slate", "Ore_Silver_Stone", "Ore_Silver_Volcanic")),
    ADAMANTITE_ORE(List.of("Ore_Adamantite_Magma")),
    MITHRIL_ORE(List.of("Ore_Mithril_Stone")),
    TREASURE_CHEST(List.of("Furniture_Dungeon_Chest_Epic"));

    private final List<String> blockIds;

    private static final Map<String, FieldsBlockType> BY_ID_MAP = new HashMap<>();

    static {
        for (final FieldsBlockType fieldsBlockType : values()) {
            for (final String blockId : fieldsBlockType.getBlockIds()) {
                BY_ID_MAP.put(blockId, fieldsBlockType);
            }
        }
    }

    public static Optional<FieldsBlockType> getByBlockId(final String blockId) {
        if (UtilString.isEmpty(blockId)) {
            return Optional.empty();
        }

        return Optional.ofNullable(BY_ID_MAP.get(blockId));
    }
}