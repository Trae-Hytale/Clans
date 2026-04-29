package me.trae.clans.clan.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.core.client.Client;

@AllArgsConstructor
@Getter
public class MemberDemoteEvent extends CustomCancellableEvent {

    private final Clan clan;
    private final PlayerRef playerRef;
    private final Client targetClient;
}