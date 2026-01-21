package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.player.ClanPlayerCancellableEvent;
import me.trae.framework.utility.objects.Chunk;

import java.util.List;

@Getter
public class ClanUnClaimAllEvent extends ClanPlayerCancellableEvent {

    private final List<Chunk> chunks;

    public ClanUnClaimAllEvent(final Clan clan, final PlayerRef player, final List<Chunk> chunks) {
        super(clan, player);

        this.chunks = chunks;
    }
}