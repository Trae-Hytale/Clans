package me.trae.clans.clan.listeners.damage;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.core.damage.events.CustomDamageEvent;

import java.util.Optional;

@Component
public class ClanSafeZoneDamageListener implements Module<ClansPlugin, ClanManager>, Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamagee() instanceof final Player damagee)) {
            return;
        }

        if (!(event.getDamager() instanceof final Player damager)) {
            return;
        }

        final World damageeWorld = damagee.getWorld();
        final World damagerWorld = damager.getWorld();
        if (damageeWorld == null || damagerWorld == null) {
            return;
        }

        final Optional<PlayerRef> damageePlayerRefOptional = UtilPlayer.getPlayerRef(damagee);
        final Optional<PlayerRef> damagerPlayerRefOptional = UtilPlayer.getPlayerRef(damager);

        if (damageePlayerRefOptional.isEmpty() || damagerPlayerRefOptional.isEmpty()) {
            return;
        }

        final PlayerRef damageePlayerRef = damageePlayerRefOptional.get();
        final PlayerRef damagerPlayerRef = damagerPlayerRefOptional.get();

        final Optional<Clan> damageeTerritoryClanOptional = this.getManager().getClanByChunk(Chunk.of(damageeWorld, damageePlayerRef.getTransform().getPosition()));
        final Optional<Clan> damagerTerritoryClanOptional = this.getManager().getClanByChunk(Chunk.of(damagerWorld, damagerPlayerRef.getTransform().getPosition()));

        Clan safeClan = null;

        if (damageeTerritoryClanOptional.map(Clan::isSafe).orElse(false)) {
            safeClan = damageeTerritoryClanOptional.orElse(null);
        } else if (damagerTerritoryClanOptional.map(Clan::isSafe).orElse(false)) {
            safeClan = damagerTerritoryClanOptional.orElse(null);
        }

        if (safeClan == null) {
            return;
        }

        event.setCancelled(true);

        final ClanRelation clanRelation = this.getManager().getClanRelationByPlayer(damagerPlayerRef, damageePlayerRef);

        UtilMessage.message(damager, "Clans", "You cannot harm %s in %s.".formatted(this.getManager().getPlayerName(clanRelation, damageePlayerRef), this.getManager().getClanName(clanRelation, safeClan)));
    }
}