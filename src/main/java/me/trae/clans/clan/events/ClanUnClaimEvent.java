package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import io.github.trae.hytale.framework.wrappers.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.trae.clans.clan.Clan;

@RequiredArgsConstructor
@Getter
@Setter
public class ClanUnClaimEvent extends CustomCancellableEvent {

    private final Clan clan;
    private final PlayerRef playerRef;
    private final Chunk chunk;

    private boolean homeDeleted;
}