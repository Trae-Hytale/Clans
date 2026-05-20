package me.trae.clans.gamer;

import io.github.trae.di.annotations.type.component.Service;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.gamer.configs.GamerConfig;
import me.trae.core.framework.shared.gamer.AbstractGamerManager;

@Getter
@Service
public class GamerManager extends AbstractGamerManager<ClansPlugin, Gamer, GamerRepository> {

    private final GamerConfig gamerConfig;

    public GamerManager(final GamerRepository repository, final GamerConfig gamerConfig) {
        super(repository);

        this.gamerConfig = gamerConfig;
    }
}