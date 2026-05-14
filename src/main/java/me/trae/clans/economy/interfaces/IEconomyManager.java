package me.trae.clans.economy.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;

public interface IEconomyManager {

    void setCoins(final PlayerRef playerRef, final int amount);

    void giveCoins(final PlayerRef playerRef, final int amount);

    void takeCoins(final PlayerRef playerRef, final int amount);

    boolean hasCoins(final PlayerRef playerRef, final int amount);

    Integer getAmount(final String arg);
}