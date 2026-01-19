package me.trae.clans;

import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import me.trae.clans.config.ConfigManager;
import me.trae.framework.base.Plugin;
import me.trae.framework.config.AbstractConfigManager;

import javax.annotation.Nonnull;

public class Clans extends Plugin {

    public Clans(@Nonnull final JavaPluginInit javaPluginInit) {
        super(javaPluginInit);
    }

    @Override
    public Class<? extends AbstractConfigManager<?>> getClassOfConfigManager() {
        return ConfigManager.class;
    }
}