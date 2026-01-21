package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.player.ClanPlayerCancellableEvent;

@Getter
public class ClanRenameEvent extends ClanPlayerCancellableEvent {

    private final String previousName;

    public ClanRenameEvent(final Clan clan, final PlayerRef player, final String previousName) {
        super(clan, player);

        this.previousName = previousName;
    }
}