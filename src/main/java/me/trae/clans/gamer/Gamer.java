package me.trae.clans.gamer;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.database.constants.Constants;
import me.trae.clans.gamer.properties.GamerProperty;
import me.trae.core.framework.gamer.AbstractGamer;

import java.util.UUID;

public class Gamer extends AbstractGamer<GamerProperty> {

    public Gamer(final UUID id) {
        super(id);
    }

    public Gamer(final PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    public Object getValueByProperty(final GamerProperty gamerProperty) {
        return Constants.EMPTY_PROPERTY;
    }
}