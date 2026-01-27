package me.trae.clans.clan.data;

import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.properties.EnemyProperty;
import me.trae.framework.database.domain.DomainData;
import me.trae.framework.database.domain.SubDomain;

import java.util.UUID;

@Getter
@Setter
public class Enemy implements SubDomain<EnemyProperty> {

    private final UUID id;

    private long systemTime;
    private int points;

    public Enemy(final UUID id, final long systemTime, final int points) {
        this.id = id;
        this.systemTime = systemTime;
        this.points = points;
    }

    public Enemy(final Clan clan) {
        this(clan.getId(), System.currentTimeMillis(), 0);
    }

    public Enemy(final DomainData<EnemyProperty> data) {
        this(data.getId(), data.get(Long.class, EnemyProperty.SYSTEM_TIME), data.get(Integer.class, EnemyProperty.POINTS));
    }

    @Override
    public Object getValueByProperty(final EnemyProperty enemyProperty) {
        return switch (enemyProperty) {
            case SYSTEM_TIME -> this.getSystemTime();
            case POINTS -> this.getPoints();
        };
    }
}