package me.trae.clans.clan.events.abstracts.player;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.ClanEvent;
import me.trae.clans.clan.events.abstracts.player.interfaces.IClanPlayerEvent;
import me.trae.core.client.Client;

@Getter
public class ClanPlayerEvent extends ClanEvent implements IClanPlayerEvent {

    private final PlayerRef player;
    private final Client playerClient;

    public ClanPlayerEvent(final Clan clan, final PlayerRef player, final Client playerClient) {
        super(clan);

        this.player = player;
        this.playerClient = playerClient;
    }
}