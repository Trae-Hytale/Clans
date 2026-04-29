package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.core.client.Client;

@AllArgsConstructor
@Getter
public class ClanCreateEvent extends CustomCancellableEvent {

    private final PlayerRef playerRef;
    private final Client playerClient;
    private final String name;
}