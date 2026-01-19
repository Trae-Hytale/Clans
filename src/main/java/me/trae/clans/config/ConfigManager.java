package me.trae.clans.config;

import me.trae.clans.Clans;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.config.AbstractConfigManager;

@Component
public class ConfigManager extends AbstractConfigManager<Clans> {

    public ConfigManager(final Clans plugin) {
        super(plugin);
    }
}