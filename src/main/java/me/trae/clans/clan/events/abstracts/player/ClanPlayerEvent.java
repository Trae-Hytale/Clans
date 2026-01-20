package me.trae.clans.clan.events.abstracts.player;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.ClanEvent;

@Getter
public class ClanPlayerEvent extends ClanEvent {

    private final PlayerRef playerRef;

    public ClanPlayerEvent(final Clan clan, final PlayerRef playerRef) {
        super(clan);

        this.playerRef = playerRef;
    }
}