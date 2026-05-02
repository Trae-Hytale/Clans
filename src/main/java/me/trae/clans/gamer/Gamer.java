package me.trae.clans.gamer;

import me.trae.clans.gamer.properties.GamerProperty;
import me.trae.core.framework.gamer.AbstractGamer;

import java.util.UUID;

public class Gamer extends AbstractGamer<GamerProperty> {

    public Gamer(final UUID id) {
        super(id);
    }

    @Override
    public Object getValueByProperty(final GamerProperty gamerProperty) {
        return null;
    }
}