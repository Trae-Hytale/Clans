package me.trae.clans.clan.data;

import io.github.trae.database.domain.data.DomainData;
import io.github.trae.database.domain.models.SubDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.data.enums.RequestType;
import me.trae.clans.clan.data.properties.RequestProperty;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.function.BiFunction;

@AllArgsConstructor
@Getter
public class Request implements SubDomain<RequestProperty> {

    public static final BiFunction<UUID, RequestType, UUID> ID_FORMATTER = (id, requestType) -> UUID.nameUUIDFromBytes(("%s:%s".formatted(id.toString(), requestType.name())).getBytes(StandardCharsets.UTF_8));

    private final UUID id;

    private final UUID targetId;
    private final RequestType type;
    private final long createdAt;

    public Request(final DomainData<RequestProperty> domainData) {
        this(domainData.getIdentifier(), domainData.get(UUID.class, RequestProperty.TARGET_ID), RequestType.valueOf(domainData.get(String.class, RequestProperty.TYPE)), domainData.get(Long.class, RequestProperty.CREATED_AT));
    }

    public Request(final UUID id, final RequestType type) {
        this(ID_FORMATTER.apply(id, type), id, type, System.currentTimeMillis());
    }

    @Override
    public Object getValueByProperty(final RequestProperty requestProperty) {
        return switch (requestProperty) {
            case TARGET_ID -> this.getTargetId();
            case TYPE -> this.getType().name();
            case CREATED_AT -> this.getCreatedAt();
        };
    }
}