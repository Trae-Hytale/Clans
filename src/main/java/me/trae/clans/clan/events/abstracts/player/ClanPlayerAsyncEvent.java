package me.trae.clans.clan.events.abstracts.player;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.ClanAsyncEvent;

@Getter
public class ClanPlayerAsyncEvent extends ClanAsyncEvent {

    private final PlayerRef player;

    public ClanPlayerAsyncEvent(final Clan clan, final PlayerRef player) {
        super(clan);

        this.player = player;
    }
}