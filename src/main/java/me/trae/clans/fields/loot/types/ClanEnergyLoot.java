package me.trae.clans.fields.loot.types;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.utilities.UtilTime;
import lombok.AllArgsConstructor;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.events.energy.ClanEnergyGainEvent;
import me.trae.clans.fields.loot.Loot;
import me.trae.core.event.BlockBreakEvent;

import java.util.function.Function;

@AllArgsConstructor
public class ClanEnergyLoot implements Loot {

    private final Function<String, Long> function;

    @Override
    public void apply(final BlockBreakEvent blockBreakEvent) {
        final ClanManager clanManager = this.getFieldsManager().getClanManager();

        if (!(clanManager.getEnergyConfig().isEnabled())) {
            return;
        }

        final PlayerRef playerRef = blockBreakEvent.getPlayerRef();

        clanManager.getClanByPlayer(playerRef).ifPresent(playerClan -> {
            final long energy = this.function.apply(blockBreakEvent.getBlockType().getId());

            if (UtilEvent.supply(new ClanEnergyGainEvent(playerClan, energy)).isCancelled()) {
                return;
            }

            UtilMessage.message(playerRef, "Fields", "You received <light_purple>%s</light_purple> of Clan Energy.".formatted(UtilTime.getTime(energy)));
        });
    }
}