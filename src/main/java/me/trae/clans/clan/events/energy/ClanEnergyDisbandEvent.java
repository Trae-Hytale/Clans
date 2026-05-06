package me.trae.clans.clan.events.energy;

import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;

@AllArgsConstructor
@Getter
public class ClanEnergyDisbandEvent extends CustomCancellableEvent {

    private final Clan clan;
}