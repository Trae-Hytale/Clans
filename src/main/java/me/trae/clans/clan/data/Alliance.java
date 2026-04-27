package me.trae.clans.clan.data;

import io.github.trae.database.domain.data.DomainData;
import io.github.trae.database.domain.models.SubDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.properties.AllianceProperty;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Alliance implements SubDomain<AllianceProperty> {

    private final UUID id;
    private final long createdAt;

    private boolean trusted;

    public Alliance(final DomainData<AllianceProperty> domainData) {
        this(domainData.getIdentifier(), domainData.get(Long.class, AllianceProperty.CREATED_AT), domainData.get(Boolean.class, AllianceProperty.TRUSTED));
    }

    public Alliance(final Clan clan) {
        this(clan.getId(), System.currentTimeMillis(), false);
    }

    @Override
    public Object getValueByProperty(final AllianceProperty allianceProperty) {
        return switch (allianceProperty) {
            case CREATED_AT -> this.getCreatedAt();
            case TRUSTED -> this.isTrusted();
        };
    }
}