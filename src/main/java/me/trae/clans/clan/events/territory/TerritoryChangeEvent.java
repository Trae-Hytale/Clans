package me.trae.clans.clan.events.territory;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TerritoryChangeEvent extends CustomEvent {

    private final PlayerRef playerRef;
}