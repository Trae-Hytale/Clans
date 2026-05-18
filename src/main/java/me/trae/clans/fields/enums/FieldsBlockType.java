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

    DIAMOND_ORE("", List.of()),
    GOLD_ORE("", List.of()),
    IRON_ORE("", List.of()),
    TREASURE_CHEST("", List.of());

    private final String blockId;
    private final List<String> droppedItemIds;

    private static final Map<String, FieldsBlockType> BY_ID_MAP = new HashMap<>();

    static {
        for (final FieldsBlockType fieldsBlockType : values()) {
            BY_ID_MAP.put(fieldsBlockType.getBlockId(), fieldsBlockType);
        }
    }

    public static Optional<FieldsBlockType> getByBlockId(final String blockId) {
        if (UtilString.isEmpty(blockId)) {
            return Optional.empty();
        }

        return Optional.ofNullable(BY_ID_MAP.get(blockId));
    }
}