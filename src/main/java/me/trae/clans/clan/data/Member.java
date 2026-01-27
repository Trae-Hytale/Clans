package me.trae.clans.clan.data;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.data.interfaces.IMember;
import me.trae.clans.clan.data.properties.MemberProperty;
import me.trae.framework.database.domain.SubDomain;
import me.trae.framework.database.domain.data.DomainData;
import me.trae.framework.utility.java.PlayerTracker;

import java.util.UUID;

@Getter
@Setter
public class Member implements IMember, SubDomain<MemberProperty> {

    private final UUID id;

    private long systemTime;
    private MemberRole role;

    public Member(final UUID id, final long systemTime, final MemberRole role) {
        this.id = id;
        this.systemTime = systemTime;
        this.role = role;
    }

    public Member(final PlayerRef playerRef, final MemberRole role) {
        this(playerRef.getUuid(), System.currentTimeMillis(), role);
    }

    public Member(final DomainData<MemberProperty> data) {
        this(data.getId(), data.get(Long.class, MemberProperty.SYSTEM_TIME), MemberRole.valueOf(data.get(String.class, MemberProperty.ROLE)));
    }

    @Override
    public boolean hasRole(final MemberRole role) {
        return this.getRole().ordinal() >= role.ordinal();
    }

    @Override
    public PlayerRef getPlayer() {
        return PlayerTracker.getOnlinePlayer(this.getId()).orElse(null);
    }

    @Override
    public boolean isOnline() {
        return PlayerTracker.isOnlinePlayer(this.getId());
    }

    @Override
    public Object getValueByProperty(final MemberProperty memberProperty) {
        return switch (memberProperty) {
            case SYSTEM_TIME -> this.getSystemTime();
            case ROLE -> this.getRole().name();
        };
    }
}