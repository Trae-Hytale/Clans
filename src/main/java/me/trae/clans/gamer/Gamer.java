package me.trae.clans.gamer;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.database.domain.data.DomainData;
import io.github.trae.utilities.UtilString;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.gamer.interfaces.IGamer;
import me.trae.clans.gamer.properties.GamerProperty;
import me.trae.core.framework.shared.gamer.AbstractGamer;

import java.util.UUID;

@Getter
@Setter
public class Gamer extends AbstractGamer<GamerProperty> implements IGamer {

    private int coins;

    public Gamer(final UUID id) {
        super(id);
    }

    public Gamer(final DomainData<GamerProperty> domainData) {
        super(domainData);

        this.coins = domainData.get(Integer.class, GamerProperty.COINS);
    }

    public Gamer(final PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    public Object getValueByProperty(final GamerProperty gamerProperty) {
        return switch (gamerProperty) {
            case COINS -> this.getCoins();
        };
    }

    @Override
    public String getFormattedCoins() {
        return UtilString.formatToDollarByInteger(this.getCoins());
    }
}