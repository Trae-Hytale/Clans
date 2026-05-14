package me.trae.clans.economy;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.utilities.UtilString;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.economy.interfaces.IEconomyManager;
import me.trae.clans.gamer.GamerManager;
import me.trae.clans.gamer.properties.GamerProperty;

@AllArgsConstructor
@Getter
@Service
public class EconomyManager implements Manager<ClansPlugin>, IEconomyManager {

    private final GamerManager gamerManager;

    @Override
    public void setCoins(final PlayerRef playerRef, final int amount) {
        this.gamerManager.getGamerByPlayer(playerRef).ifPresent(gamer -> {
            gamer.setCoins(amount);
            this.gamerManager.getRepository().update(gamer, GamerProperty.COINS);
        });
    }

    @Override
    public void giveCoins(final PlayerRef playerRef, final int amount) {
        this.gamerManager.getGamerByPlayer(playerRef).ifPresent(gamer -> {
            gamer.setCoins(gamer.getCoins() + amount);
            this.gamerManager.getRepository().update(gamer, GamerProperty.COINS);
        });
    }

    @Override
    public void takeCoins(final PlayerRef playerRef, final int amount) {
        this.gamerManager.getGamerByPlayer(playerRef).ifPresent(gamer -> {
            gamer.setCoins(gamer.getCoins() - amount);
            this.gamerManager.getRepository().update(gamer, GamerProperty.COINS);
        });
    }

    @Override
    public boolean hasCoins(final PlayerRef playerRef, final int amount) {
        return this.gamerManager.getGamerByPlayer(playerRef).map(gamer -> gamer.getCoins() >= amount).orElse(false);
    }

    @Override
    public Integer getAmount(final String arg) {
        if (!(UtilString.isEmpty(arg))) {
            try {
                return Integer.parseInt(arg);
            } catch (final Exception ignored) {
            }
        }

        return null;
    }
}