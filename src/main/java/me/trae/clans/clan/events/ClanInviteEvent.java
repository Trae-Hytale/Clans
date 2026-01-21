package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.player.ClanPlayerCancellableEvent;

@Getter
public class ClanInviteEvent extends ClanPlayerCancellableEvent {

    private final PlayerRef target;

    public ClanInviteEvent(final Clan clan, final PlayerRef player, final PlayerRef target) {
        super(clan, player);

        this.target = target;
    }
}