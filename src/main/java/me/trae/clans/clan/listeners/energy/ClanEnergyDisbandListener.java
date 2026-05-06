package me.trae.clans.clan.listeners.energy;

import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.events.energy.ClanEnergyDisbandEvent;

@Component
public class ClanEnergyDisbandListener implements Module<ClansPlugin, ClanManager>, EventListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanEnergyDisband(final ClanEnergyDisbandEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().disbandClan(event.getClan());
    }
}