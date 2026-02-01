package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.player.ClanPlayerCancellableEvent;
import me.trae.core.client.Client;

@Getter
public class ClanCreateEvent extends ClanPlayerCancellableEvent {

    public ClanCreateEvent(final Clan clan, final PlayerRef player, final Client playerClient) {
        super(clan, player, playerClient);
    }
}