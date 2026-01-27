package me.trae.clans.clan.data;

import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.properties.AllianceProperty;
import me.trae.framework.database.domain.data.DomainData;
import me.trae.framework.database.domain.SubDomain;

import java.util.UUID;

@Getter
@Setter
public class Alliance implements SubDomain<AllianceProperty> {

    private final UUID id;

    private long systemTime;
    private boolean trusted;

    public Alliance(final UUID id, final long systemTime, final boolean trusted) {
        this.id = id;
        this.systemTime = systemTime;
        this.trusted = trusted;
    }

    public Alliance(final Clan clan) {
        this(clan.getId(), System.currentTimeMillis(), false);
    }

    public Alliance(final DomainData<AllianceProperty> data) {
        this(data.getId(), data.get(Long.class, AllianceProperty.SYSTEM_TIME), data.get(Boolean.class, AllianceProperty.TRUSTED));
    }

    @Override
    public Object getValueByProperty(final AllianceProperty allianceProperty) {
        return switch (allianceProperty) {
            case SYSTEM_TIME -> this.getSystemTime();
            case TRUSTED -> this.isTrusted();
        };
    }
}