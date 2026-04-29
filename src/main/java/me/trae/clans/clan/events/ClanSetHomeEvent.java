package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;

@AllArgsConstructor
@Getter
public class ClanSetHomeEvent extends CustomCancellableEvent {

    private final Clan clan;
    private final PlayerRef playerRef;
    private final BlockLocation blockLocation;
}