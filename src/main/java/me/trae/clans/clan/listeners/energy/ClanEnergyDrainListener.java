package me.trae.clans.clan.listeners.energy;

import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.events.energy.ClanEnergyDrainEvent;
import me.trae.clans.clan.properties.ClanProperty;

@Component
public class ClanEnergyDrainListener implements Module<ClansPlugin, ClanManager>, Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanEnergyDrain(final ClanEnergyDrainEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.hasAmount())) {
            return;
        }

        final Clan clan = event.getClan();

        clan.takeEnergy(event.getAmount());
        this.getManager().getRepository().update(clan, ClanProperty.ENERGY);
    }
}