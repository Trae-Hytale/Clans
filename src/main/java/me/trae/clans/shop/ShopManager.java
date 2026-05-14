package me.trae.clans.shop;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.utilities.UtilString;
import lombok.AllArgsConstructor;
import me.trae.clans.ClansPlugin;
import me.trae.clans.economy.EconomyManager;
import me.trae.clans.shop.interfaces.IShopManager;

@AllArgsConstructor
@Service
public class ShopManager implements Manager<ClansPlugin>, IShopManager {

    private final EconomyManager economyManager;

    @Override
    public boolean purchase(final PlayerRef playerRef, final ShopItem<?> shopItem, final int amount) {
        if (shopItem.getBuyPrice() == 0) {
            UtilMessage.message(playerRef, "Shop", "You cannot purchase this!");
            return false;
        }

        final int cost = shopItem.getBuyPrice() * amount;

        if (this.economyManager.hasCoins(playerRef, cost)) {
            this.economyManager.takeCoins(playerRef, cost);

            if (shopItem.getMaxBuyQuantity() > 0) {
                UtilMessage.message(playerRef, "Shop", "You have purchased <green>%s</green> of %s for <gold>%s</gold>.".formatted(amount, shopItem.getDisplayName(), UtilString.formatToDollarByInteger(cost)));
            } else {
                UtilMessage.message(playerRef, "Shop", "You have purchased %s for <gold>%s</gold>.".formatted(shopItem.getDisplayName(), UtilString.formatToDollarByInteger(cost)));
            }

            shopItem.onPurchase(playerRef, amount);
            return true;
        }

        UtilMessage.message(playerRef, "Shop", "You have insufficient funds to purchase this!");

        return false;
    }

    @Override
    public boolean sell(final PlayerRef playerRef, final ShopItem<?> shopItem, final int amount) {
        if (shopItem.getSellPrice() == 0) {
            UtilMessage.message(playerRef, "Shop", "You cannot sell this!");
            return false;
        }

        final int profit = shopItem.getSellPrice() * amount;

        // Wrap the following code in an if statement for has sufficient resources check
        this.economyManager.takeCoins(playerRef, profit);

        if (shopItem.getMaxSellQuantity() > 0) {
            UtilMessage.message(playerRef, "Shop", "You have sold <green>%s</green> of %s for <gold>%s</gold>.".formatted(amount, shopItem.getDisplayName(), UtilString.formatToDollarByInteger(profit)));
        } else {
            UtilMessage.message(playerRef, "Shop", "You have sold %s for <gold>%s</gold>.".formatted(shopItem.getDisplayName(), UtilString.formatToDollarByInteger(profit)));
        }

        shopItem.onSell(playerRef, amount);

        return true;
    }
}