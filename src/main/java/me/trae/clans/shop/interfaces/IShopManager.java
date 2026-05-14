package me.trae.clans.shop.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.shop.ShopItem;

public interface IShopManager {

    boolean purchase(final PlayerRef playerRef, final ShopItem<?> shopItem, final int amount);

    boolean sell(final PlayerRef playerRef, final ShopItem<?> shopItem, final int amount);
}