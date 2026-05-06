package me.trae.clans.clan.listeners.damage;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.core.damage.events.CustomDamageEvent;

import java.util.Optional;

@Component
public class ClanRelationDamageListener implements Module<ClansPlugin, ClanManager>, Listener {

    @EventHandler(priority = EventPriority.NORMAL)
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

        final Optional<PlayerRef> damageePlayerRefOptional = UtilPlayer.getPlayerRef(damagee);
        final Optional<PlayerRef> damagerPlayerRefOptional = UtilPlayer.getPlayerRef(damager);

        if (damageePlayerRefOptional.isEmpty() || damagerPlayerRefOptional.isEmpty()) {
            return;
        }

        final PlayerRef damageePlayerRef = damageePlayerRefOptional.get();
        final PlayerRef damagerPlayerRef = damagerPlayerRefOptional.get();

        if (this.getManager().canHurt(damageePlayerRef, damagerPlayerRef)) {
            return;
        }

        event.setCancelled(true);

        UtilMessage.message(damager, "Clans", "You cannot harm %s.".formatted(this.getManager().getPlayerName(this.getManager().getClanRelationByPlayer(damagerPlayerRef, damageePlayerRef), damageePlayerRef)));
    }
}