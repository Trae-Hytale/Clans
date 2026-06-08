package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomEvent;
import io.github.trae.hytale.framework.wrappers.Chunk;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
public class TerritoryMassClaimEvent extends CustomEvent {

    private final Clan clan;
    private final PlayerRef playerRef;
    private final List<Chunk> chunks;
    private final Set<Clan> affectedClans;
}