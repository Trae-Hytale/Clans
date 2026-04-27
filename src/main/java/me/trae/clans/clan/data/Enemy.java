package me.trae.clans.clan.data;

import io.github.trae.database.domain.data.DomainData;
import io.github.trae.database.domain.models.SubDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.properties.EnemyProperty;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Enemy implements SubDomain<EnemyProperty> {

    private final UUID id;
    private final long createdAt;

    private int points;

    public Enemy(final DomainData<EnemyProperty> domainData) {
        this(domainData.getIdentifier(), domainData.get(Long.class, EnemyProperty.CREATED_AT), domainData.get(Integer.class, EnemyProperty.POINTS));
    }

    public Enemy(final Clan clan) {
        this(clan.getId(), System.currentTimeMillis(), 0);
    }

    @Override
    public Object getValueByProperty(final EnemyProperty enemyProperty) {
        return switch (enemyProperty) {
            case CREATED_AT -> this.getCreatedAt();
            case POINTS -> this.getPoints();
        };
    }
}