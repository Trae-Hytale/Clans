package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.player.ClanPlayerCancellableEvent;
import me.trae.framework.utility.objects.Chunk;

@Getter
public class ClanClaimEvent extends ClanPlayerCancellableEvent {

    private final Chunk chunk;

    public ClanClaimEvent(final Clan clan, final PlayerRef player, final Chunk chunk) {
        super(clan, player);

        this.chunk = chunk;
    }
}