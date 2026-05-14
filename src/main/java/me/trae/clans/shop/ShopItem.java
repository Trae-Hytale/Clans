package me.trae.clans.shop;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hf.SubModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.shop.interfaces.IShopItem;

@AllArgsConstructor
@Getter
public class ShopItem<Module extends Shop> implements SubModule<ClansPlugin, Module>, IShopItem {

    private final String displayName;
    private final int buyPrice, sellPrice, maxBuyQuantity, maxSellQuantity;

    @Override
    public void onPurchase(final PlayerRef playerRef, final int amount) {
    }

    @Override
    public void onSell(final PlayerRef playerRef, final int amount) {
    }
}