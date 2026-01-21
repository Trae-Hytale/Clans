package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.player.ClanPlayerCancellableEvent;

public class ClanDisbandEvent extends ClanPlayerCancellableEvent {

    public ClanDisbandEvent(final Clan clan, final PlayerRef player) {
        super(clan, player);
    }
}