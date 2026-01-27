package me.trae.clans.clan.data;

import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.properties.PillageProperty;
import me.trae.framework.database.domain.SubDomain;
import me.trae.framework.database.domain.data.DomainData;

import java.util.UUID;

@Getter
@Setter
public class Pillage implements SubDomain<PillageProperty> {

    private final UUID id;

    private long systemTime;

    public Pillage(final UUID id, final long systemTime) {
        this.id = id;
        this.systemTime = systemTime;
    }

    public Pillage(final Clan clan) {
        this(clan.getId(), System.currentTimeMillis());
    }

    public Pillage(final DomainData<PillageProperty> data) {
        this(data.getId(), data.get(Long.class, PillageProperty.SYSTEM_TIME));
    }

    @Override
    public Object getValueByProperty(final PillageProperty pillageProperty) {
        return null;
    }
}