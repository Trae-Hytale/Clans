package me.trae.clans.shop.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;

public interface IShopItem {

    void onPurchase(final PlayerRef playerRef, final int amount);

    void onSell(final PlayerRef playerRef, final int amount);
}