package me.trae.clans.clan.listeners.teleport;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.wrappers.Chunk;
import lombok.AllArgsConstructor;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.commands.subcommands.configs.HomeCommandConfig;
import me.trae.clans.clan.teleport.ClanHomeTeleportData;
import me.trae.core.teleport.events.PlayerPreTeleportEvent;

@AllArgsConstructor
@Component
public class ClanHomeTeleportFromTerritoryListener implements Module<ClansPlugin, ClanManager>, EventListener {

    private final HomeCommandConfig homeCommandConfig;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerPreTeleport(final PlayerPreTeleportEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getTeleportData() instanceof final ClanHomeTeleportData clanHomeTeleportData)) {
            return;
        }

        final Player player = clanHomeTeleportData.getPlayer();

        final World world = player.getWorld();
        if (world == null) {
            return;
        }

        final PlayerRef playerRef = clanHomeTeleportData.getPlayerRef();

        this.getManager().getClanByPlayer(playerRef).ifPresent(playerClan -> {
            this.getManager().getClanByChunk(Chunk.of(world, playerRef.getTransform().getPosition())).ifPresent(territoryClan -> {
                if (territoryClan.equals(playerClan)) {
                    if (this.homeCommandConfig.getSelfTerritoryTeleportDuration() >= 0L) {
                        clanHomeTeleportData.setDuration(this.homeCommandConfig.getSelfTerritoryTeleportDuration());
                    }
                    return;
                }

                if (territoryClan.isAllianceByClan(playerClan)) {
                    if (this.homeCommandConfig.getAllianceTerritoryTeleportDuration() >= 0L) {
                        clanHomeTeleportData.setDuration(this.homeCommandConfig.getAllianceTerritoryTeleportDuration());
                    }
                    return;
                }

                if (playerClan.isPillageByClan(territoryClan)) {
                    if (this.homeCommandConfig.getPillageTerritoryTeleportDuration() >= 0L) {
                        clanHomeTeleportData.setDuration(this.homeCommandConfig.getPillageTerritoryTeleportDuration());
                    }
                }
            });
        });
    }
}