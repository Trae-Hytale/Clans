package me.trae.clans.clan.listeners.teleport;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.teleport.ClanHomeTeleportData;
import me.trae.core.teleport.events.PlayerPreTeleportEvent;

import java.util.Optional;

@Component
public class ClanHomeTeleportDurationFromSpawn implements Module<ClansPlugin, ClanManager>, EventListener {

    @EventHandler(priority = EventPriority.HIGH)
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

        final Optional<PlayerRef> playerRefOptional = UtilPlayer.getPlayerRef(player);
        if (playerRefOptional.isEmpty()) {
            return;
        }

        final PlayerRef playerRef = playerRefOptional.get();

        this.getManager().getClanByChunk(Chunk.of(world, playerRef.getTransform().getPosition())).ifPresent(territoryClan -> {
            if (!(territoryClan.isAdmin())) {
                return;
            }

            if (!(territoryClan.getName().toLowerCase().contains("spawn"))) {
                return;
            }

            clanHomeTeleportData.setDuration(0L);
        });
    }
}