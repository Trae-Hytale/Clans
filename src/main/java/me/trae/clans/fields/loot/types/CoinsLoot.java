package me.trae.clans.fields.loot.types;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.utilities.UtilString;
import lombok.AllArgsConstructor;
import me.trae.clans.economy.EconomyManager;
import me.trae.clans.fields.loot.Loot;
import me.trae.core.event.BlockBreakEvent;

import java.util.function.Function;

@AllArgsConstructor
public class CoinsLoot implements Loot {

    private final EconomyManager economyManager;
    private final Function<String, Integer> function;

    @Override
    public void apply(final BlockBreakEvent blockBreakEvent) {
        final PlayerRef playerRef = blockBreakEvent.getPlayerRef();

        final int coins = function.apply(blockBreakEvent.getBlockType().getId());

        this.economyManager.giveCoins(playerRef, coins);

        UtilMessage.message(playerRef, "Fields", "You received <gold>%s</gold> Coins.".formatted(UtilString.formatToDollarByInteger(coins)));
    }
}