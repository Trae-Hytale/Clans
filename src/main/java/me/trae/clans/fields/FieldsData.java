package me.trae.clans.fields;

import io.github.trae.database.domain.data.DomainData;
import io.github.trae.database.domain.models.Domain;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import io.github.trae.utilities.objects.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.fields.properties.FieldsDataProperty;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class FieldsData implements Domain<FieldsDataProperty> {

    public static final Function<BlockLocation, UUID> ID_FORMATTER = blockLocation -> UUID.nameUUIDFromBytes(blockLocation.toString().getBytes(StandardCharsets.UTF_8));

    private final UUID id;

    private final BlockLocation location;
    private final String blockId;

    public FieldsData(final BlockLocation location, final String blockId) {
        this(ID_FORMATTER.apply(location), location, blockId);
    }

    public FieldsData(final DomainData<FieldsDataProperty> domainData) {
        this(domainData.getIdentifier(), BlockLocation.deserialize(domainData.getMap(String.class, Object.class, FieldsDataProperty.LOCATION)), domainData.get(String.class, FieldsDataProperty.BLOCK_ID));
    }

    @Override
    public Object getValueByProperty(final FieldsDataProperty fieldsDataProperty) {
        return switch (fieldsDataProperty) {
            case LOCATION -> BlockLocation.serialize(this.getLocation());
            case BLOCK_ID -> this.getBlockId();
        };
    }
}