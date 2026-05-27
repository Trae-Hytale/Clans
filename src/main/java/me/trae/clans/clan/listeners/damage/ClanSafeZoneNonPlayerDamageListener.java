package me.trae.clans.clan.listeners.damage;

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
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.core.damage.events.CustomDamageEvent;

import java.util.Optional;

@Component
public class ClanSafeZoneNonPlayerDamageListener implements Module<ClansPlugin, ClanManager>, EventListener {

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getDamager() instanceof Player) {
            return;
        }

        if (!(event.getDamagee() instanceof final Player damageePlayer)) {
            return;
        }

        final World damageePlayerWorld = damageePlayer.getWorld();
        if (damageePlayerWorld == null) {
            return;
        }

        final PlayerRef damageePlayerRef = UtilPlayer.getPlayerRef(damageePlayer).orElse(null);
        if (damageePlayerRef == null) {
            return;
        }

        final Optional<Clan> territoryClanOptional = this.getManager().getClanByChunk(Chunk.of(damageePlayerWorld, damageePlayerRef.getTransform().getPosition()));
        if (territoryClanOptional.isEmpty()) {
            return;
        }

        final Clan territoryClan = territoryClanOptional.get();

        if (!(territoryClan.isSafe())) {
            return;
        }

        event.setCancelledWithReason(this.getFrameName());
    }
}