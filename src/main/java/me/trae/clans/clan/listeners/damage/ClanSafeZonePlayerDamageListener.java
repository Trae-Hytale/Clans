package me.trae.clans.clan.listeners.damage;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.core.client.Client;
import me.trae.core.damage.events.CustomDamageEvent;

import java.util.Optional;

@Component
public class ClanSafeZonePlayerDamageListener implements Module<ClansPlugin, ClanManager>, EventListener {

    @EventHandler(priority = EventPriority.LOW)
    public void onCustomDamage(final CustomDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getDamagee() instanceof final Player damageePlayer)) {
            return;
        }

        if (!(event.getDamager() instanceof final Player damagerPlayer)) {
            return;
        }

        final World damageePlayerWorld = damageePlayer.getWorld();
        final World damagerPlayerWorld = damagerPlayer.getWorld();
        if (damageePlayerWorld == null || damagerPlayerWorld == null) {
            return;
        }

        final PlayerRef damageePlayerRef = UtilPlayer.getPlayerRef(damageePlayer).orElse(null);
        final PlayerRef damagerPlayerRef = UtilPlayer.getPlayerRef(damagerPlayer).orElse(null);
        if (damageePlayerRef == null || damagerPlayerRef == null) {
            return;
        }

        final Optional<Clan> damageeTerritoryClanOptional = this.getManager().getClanByChunk(Chunk.of(damageePlayerWorld, damageePlayerRef.getTransform().getPosition()));
        final Optional<Clan> damagerTerritoryClanOptional = this.getManager().getClanByChunk(Chunk.of(damagerPlayerWorld, damagerPlayerRef.getTransform().getPosition()));

        Clan safeClan = null;

        if (damageeTerritoryClanOptional.map(Clan::isSafe).orElse(false)) {
            safeClan = damageeTerritoryClanOptional.orElse(null);
        } else if (damagerTerritoryClanOptional.map(Clan::isSafe).orElse(false)) {
            safeClan = damagerTerritoryClanOptional.orElse(null);
        }

        if (safeClan == null) {
            return;
        }

        if (this.getManager().getClientManager().getClientByPlayer(damagerPlayerRef).map(Client::isAdministrating).orElse(false)) {
            return;
        }

        event.setCancelledWithReason(this.getFrameName());

        final ClanRelation clanRelation = this.getManager().getClanRelationByPlayer(damagerPlayerRef, damageePlayerRef);

        UtilMessage.message(damagerPlayerRef, "Clans", "You cannot harm %s in %s.".formatted(this.getManager().getPlayerName(clanRelation, damageePlayerRef), this.getManager().getClanName(clanRelation, safeClan)));
    }
}