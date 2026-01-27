package me.trae.clans;

import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.event.events.player.DrainPlayerFromWorldEvent;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import me.trae.framework.base.Plugin;

import javax.annotation.Nonnull;

public class Clans extends Plugin {

    public Clans(@Nonnull final JavaPluginInit javaPluginInit) {
        super(javaPluginInit);

        DrainPlayerFromWorldEvent drainPlayerFromWorldEvent;
        AddPlayerToWorldEvent addPlayerToWorldEvent;
    }
}