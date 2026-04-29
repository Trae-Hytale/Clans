package me.trae.clans.clan.listeners.death;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.core.death.events.CustomDeathEvent;
import me.trae.core.death.events.CustomDeathMessageEvent;

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
            this.getName(player, targetPlayerRef).ifPresent(event::setKillerName);
        }
    }

    private Optional<String> getName(final Player player, final PlayerRef targetPlayerRef) {
        final Ref<EntityStore> playerReference = player.getReference();
        final World playerWorld = player.getWorld();

        if (playerReference == null || playerWorld == null) {
            return Optional.empty();
        }

        final PlayerRef playerRef = playerWorld.getEntityStore().getStore().getComponent(playerReference, PlayerRef.getComponentType());

        if (playerRef == null) {
            return Optional.empty();
        }

        return Optional.of(this.getManager().getPlayerName(this.getManager().getClanRelationByPlayer(playerRef, targetPlayerRef), playerRef));
    }
}