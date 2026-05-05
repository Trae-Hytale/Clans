package me.trae.clans.clan.events.energy;

import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.Clan;

@AllArgsConstructor
@Getter
@Setter
public class ClanEnergyGainEvent extends CustomCancellableEvent {

    private final Clan clan;

    private long amount;
}