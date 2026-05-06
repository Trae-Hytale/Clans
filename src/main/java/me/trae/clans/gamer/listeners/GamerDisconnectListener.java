package me.trae.clans.gamer.listeners;

import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import me.trae.clans.ClansPlugin;
import me.trae.clans.gamer.GamerManager;

@Component
public class GamerDisconnectListener implements Module<ClansPlugin, GamerManager>, EventListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        this.getManager().handlePlayerDisconnect(event.getPlayerRef());
    }
}