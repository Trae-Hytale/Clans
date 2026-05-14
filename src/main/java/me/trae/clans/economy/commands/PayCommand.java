package me.trae.clans.economy.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.command.PlayerCommand;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.utilities.UtilString;
import me.trae.clans.ClansPlugin;
import me.trae.clans.economy.EconomyManager;
import me.trae.core.client.enums.Rank;

@Component
public class PayCommand extends PlayerCommand<ClansPlugin, EconomyManager> {

    public PayCommand() {
        super("pay", "Send Coins to a Player", Rank.DEFAULT);

        this.addAliases("sendmoney", "sendcoins");
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        if (args.length == 0) {
            UtilMessage.message(playerRef, "Economy", "You did not input a Player.");
            return;
        }

        if (args.length == 1) {
            UtilMessage.message(playerRef, "Economy", "You did not input a Amount.");
            return;
        }

        final Integer amount = this.getManager().getAmount(args[1]);
        if (amount == null) {
            UtilMessage.message(playerRef, "Economy", "You did not input a valid Amount.");
            return;
        }

        if (amount <= 0) {
            UtilMessage.message(playerRef, "Economy", "Amount must be greater than Zero.");
            return;
        }

        if (!(this.getManager().hasCoins(playerRef, amount))) {
            UtilMessage.message(playerRef, "Economy", "You have insufficient funds to send <gold>%s</gold>.".formatted(UtilString.formatToDollarByInteger(amount)));
            return;
        }

        UtilPlayer.searchPlayerRef(playerRef, args[0], true).ifPresent(targetPlayer -> {
            this.getManager().takeCoins(playerRef, amount);
            this.getManager().giveCoins(targetPlayer, amount);

            UtilMessage.message(playerRef, "Economy", "You sent <gold>%s</gold> to <yellow>%s</yellow>.".formatted(UtilString.formatToDollarByInteger(amount), targetPlayer.getUsername()));
            UtilMessage.message(targetPlayer, "Economy", "<yellow>%s</yellow> sent <gold>%s</gold> to you.".formatted(playerRef.getUsername(), UtilString.formatToDollarByInteger(amount)));
        });
    }
}