package me.trae.clans;

import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import io.github.trae.di.annotations.type.Application;
import me.trae.core.CorePlugin;
import me.trae.core.framework.Plugin;

import javax.annotation.Nonnull;

@Application(dependencies = CorePlugin.class)
public class ClansPlugin extends Plugin {

    public ClansPlugin(@Nonnull final JavaPluginInit javaPluginInit) {
        super(javaPluginInit);
    }
}