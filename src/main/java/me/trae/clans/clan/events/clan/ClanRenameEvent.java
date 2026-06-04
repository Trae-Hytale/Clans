package me.trae.clans.clan.events.clan;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;

@AllArgsConstructor
@Getter
public class ClanRenameEvent extends CustomCancellableEvent {

    private final Clan clan;
    private final PlayerRef playerRef;
    private final String name;
}