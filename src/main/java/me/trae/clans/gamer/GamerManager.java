package me.trae.clans.gamer;

import io.github.trae.di.annotations.type.component.Service;
import me.trae.clans.ClansPlugin;
import me.trae.core.framework.gamer.AbstractGamerManager;

@Service
public class GamerManager extends AbstractGamerManager<ClansPlugin, Gamer, GamerRepository> {

    public GamerManager(final GamerRepository repository) {
        super(repository);
    }
}