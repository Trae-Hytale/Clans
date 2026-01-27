package me.trae.clans.clan.data;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;

import java.util.UUID;

@Getter
public class Request {

    private final UUID id;
    private final long systemTime;

    public Request(final UUID id, final long systemTime) {
        this.id = id;
        this.systemTime = systemTime;
    }

    public Request(final Clan clan) {
        this(clan.getId(), System.currentTimeMillis());
    }

    public Request(final PlayerRef player) {
        this(player.getUuid(), System.currentTimeMillis());
    }
}