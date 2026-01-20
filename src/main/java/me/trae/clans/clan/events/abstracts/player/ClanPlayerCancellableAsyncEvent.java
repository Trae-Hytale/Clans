package me.trae.clans.clan.events.abstracts.player;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.ClanCancellableAsyncEvent;

@Getter
public class ClanPlayerCancellableAsyncEvent extends ClanCancellableAsyncEvent {

    private final PlayerRef playerRef;

    public ClanPlayerCancellableAsyncEvent(final Clan clan, final PlayerRef playerRef) {
        super(clan);

        this.playerRef = playerRef;
    }
}