package me.trae.clans.clan.listeners;

import com.hypixel.hytale.server.core.NameMatching;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilColor;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.core.death.events.CustomDeathEvent;
import me.trae.core.death.systems.CustomDeathMessageEvent;

import java.util.Optional;

@Component
public class DeathMessageForClanListener implements Module<ClansPlugin, ClanManager>, Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onCustomDeathMessage(final CustomDeathMessageEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final CustomDeathEvent deathEvent = event.getDeathEvent();

        final PlayerRef targetPlayerRef = event.getTargetPlayerRef();

        if (deathEvent.getEntity() instanceof final Player player) {
            this.getName(player, targetPlayerRef).ifPresent(event::setEntityName);
        }

        if (deathEvent.getKiller() instanceof final Player player) {
            this.getName(player, targetPlayerRef).ifPresent(event::setEntityName);
        }
    }

    private Optional<String> getName(final Player player, final PlayerRef targetPlayerRef) {
        final PlayerRef playerRef = Universe.get().getPlayerByUsername(player.getDisplayName(), NameMatching.EXACT);
        if (playerRef == null) {
            return Optional.empty();
        }

        return Optional.of(UtilColor.serialize(this.getManager().getClanRelationByPlayer(playerRef, targetPlayerRef).getSuffix(), playerRef.getUsername()));
    }
}