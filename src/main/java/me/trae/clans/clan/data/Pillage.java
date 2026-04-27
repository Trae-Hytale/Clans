package me.trae.clans.clan.data;

import io.github.trae.database.domain.data.DomainData;
import io.github.trae.database.domain.models.SubDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.properties.PillageProperty;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Pillage implements SubDomain<PillageProperty> {

    private final UUID id;
    private final long createdAt;

    public Pillage(final DomainData<PillageProperty> domainData) {
        this(domainData.getIdentifier(), domainData.get(Long.class, PillageProperty.CREATED_AT));
    }

    public Pillage(final Clan clan) {
        this(clan.getId(), System.currentTimeMillis());
    }

    @Override
    public Object getValueByProperty(final PillageProperty pillageProperty) {
        return switch (pillageProperty) {
            case CREATED_AT -> this.getCreatedAt();
        };
    }
}