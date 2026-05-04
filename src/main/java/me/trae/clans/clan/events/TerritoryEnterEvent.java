package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;

@AllArgsConstructor
@Getter
public class TerritoryEnterEvent extends CustomEvent {

    private final PlayerRef playerRef;
    private final Clan clan;
}