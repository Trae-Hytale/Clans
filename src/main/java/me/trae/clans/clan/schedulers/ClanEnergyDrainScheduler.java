package me.trae.clans.clan.schedulers;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.trae.di.annotations.method.Scheduler;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.utilities.UtilJava;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.configs.EnergyConfig;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.energy.ClanEnergyDrainEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.config.events.ConfigReloadEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ClanEnergyDrainScheduler implements Module<ClansPlugin, ClanManager>, Listener {

    private List<Long> lastAlertIntervals;

    @Scheduler(period = 1, unit = TimeUnit.MINUTES)
    public void onScheduler() {
        if (!(this.getManager().getEnergyConfig().isEnabled())) {
            return;
        }

        for (final Clan clan : this.getManager().getClans()) {
            if (!(clan.canDepleteEnergy())) {
                continue;
            }

            final ClanEnergyDrainEvent clanEnergyDrainEvent = UtilEvent.supply(new ClanEnergyDrainEvent(clan, clan.getEnergyDepletion()));

            if (clanEnergyDrainEvent.isCancelled()) {
                continue;
            }

            final long beforeEnergy = clan.getEnergy();

            clan.takeEnergy(clanEnergyDrainEvent.getAmount());
            this.getManager().getRepository().update(clan, ClanProperty.ENERGY);

            final long afterEnergy = clan.getEnergy();

            if (beforeEnergy > 0L && afterEnergy <= 0L) {
                this.getManager().messageClan(clan, "Clans", "<red>Your Clan has ran out of energy!</red>", null);

                this.getManager().disbandClan(clan);

                for (final PlayerRef targetPlayerRef : Universe.get().getPlayers()) {
                    final ClanRelation clanRelation = this.getManager().getClanRelationByClan(this.getManager().getClanByPlayer(targetPlayerRef).orElse(null), clan);

                    UtilMessage.message(targetPlayerRef, "Clans", "%s has been disbanded for running out of energy!".formatted(this.getManager().getClanFullName(clanRelation, clan)));
                }
                continue;
            }

            final long beforeRemaining = beforeEnergy / Math.max(1, clan.getTerritory().size());
            final long afterRemaining = afterEnergy / Math.max(1, clan.getTerritory().size());

            for (final long threshold : this.getLastAlertIntervals()) {
                if (beforeRemaining >= threshold && afterRemaining < threshold) {
                    this.getManager().messageClan(clan, "Clans", "Your Clan has <green>%s</green> of energy remaining!".formatted(clan.getFormattedEnergyRemaining()), null);
                    break;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onConfigReload(final ConfigReloadEvent event) {
        if (event.getClasses().contains(EnergyConfig.class)) {
            this.updateAlertIntervals();
        }
    }

    private List<Long> getLastAlertIntervals() {
        if (this.lastAlertIntervals == null) {
            this.updateAlertIntervals();
        }

        return this.lastAlertIntervals;
    }

    private void updateAlertIntervals() {
        this.lastAlertIntervals = UtilJava.createCollection(new ArrayList<>(), list -> {
            for (final String alertInterval : this.getManager().getEnergyConfig().getAlertIntervals()) {
                io.github.trae.utilities.enums.TimeUnit.parseByInput(alertInterval).ifPresent(list::add);
            }
        });
    }
}