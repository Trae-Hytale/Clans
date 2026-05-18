package me.trae.clans.fields;

import io.github.trae.database.domain.data.DomainData;
import io.github.trae.database.domain.models.Domain;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import io.github.trae.utilities.objects.function.Function;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.fields.properties.FieldsBlockProperty;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class FieldsBlock implements Domain<FieldsBlockProperty> {

    public static final Function<BlockLocation, UUID> ID_FORMATTER = blockLocation -> UUID.nameUUIDFromBytes(blockLocation.toString().getBytes(StandardCharsets.UTF_8));

    private final UUID id;

    private final BlockLocation location;
    private final String blockId;

    public FieldsBlock(final BlockLocation location, final String blockId) {
        this(ID_FORMATTER.apply(location), location, blockId);
    }

    public FieldsBlock(final DomainData<FieldsBlockProperty> domainData) {
        this(domainData.getIdentifier(), BlockLocation.deserialize(domainData.getMap(String.class, Object.class, FieldsBlockProperty.LOCATION)), domainData.get(String.class, FieldsBlockProperty.BLOCK_ID));
    }

    @Override
    public Object getValueByProperty(final FieldsBlockProperty fieldsProperty) {
        return switch (fieldsProperty) {
            case LOCATION -> BlockLocation.serialize(this.getLocation());
            case BLOCK_ID -> this.getBlockId();
        };
    }
}