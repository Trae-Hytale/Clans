package me.trae.clans.clan.events.energy;

import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.energy.interfaces.IEnergyEvent;

@AllArgsConstructor
@Getter
@Setter
public class ClanEnergyDrainEvent extends CustomCancellableEvent implements IEnergyEvent {

    private final Clan clan;

    private long amount;
}