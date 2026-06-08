package me.trae.clans.economy.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hytale.framework.command.suggestion.Suggestion;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.utilities.UtilString;
import me.trae.clans.ClansPlugin;
import me.trae.clans.economy.EconomyManager;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.Command;
import me.trae.core.command.SubCommand;

import java.util.List;

@Service
public class EconomyCommand extends Command<ClansPlugin, EconomyManager, PlayerRef> {

    public EconomyCommand() {
        super("economy", "Economy management");

        this.addAliases("eco", "coins", "money", "balance", "bal");
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        this.getManager().getClansGamerManager().getGamerByPlayer(playerRef).ifPresent(gamer -> {
            UtilMessage.message(playerRef, "Economy", "You have <gold>%s</gold>.".formatted(gamer.getFormattedCoins()));
        });
    }

    @Component
    private static class SetCommand extends SubCommand<ClansPlugin, EconomyCommand, PlayerRef> {

        public SetCommand() {
            super("set", "Set Coins for a Player", Rank.ADMIN);
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

            final Integer amount = this.getModule().getManager().getAmount(args[1]);
            if (amount == null) {
                UtilMessage.message(playerRef, "Economy", "You did not input a valid Amount.");
                return;
            }

            UtilPlayer.searchPlayerRef(playerRef, args[0], true).ifPresent(targetPlayerRef -> {
                this.getModule().getManager().setCoins(targetPlayerRef, amount);

                UtilMessage.message(playerRef, "Economy", "You have updated the coins for <yellow>%s</yellow> to <gold>%s</gold>.".formatted(targetPlayerRef.getUsername(), UtilString.formatToDollarByInteger(amount)));
                UtilMessage.message(targetPlayerRef, "Economy", "<yellow>%s</yellow> has updated your coins to <gold>%s</gold>.".formatted(playerRef.getUsername(), UtilString.formatToDollarByInteger(amount)));
            });
        }

        @Override
        public List<Suggestion> getSuggestions() {
            return List.of(
                    new Suggestion("Player", "player", "Provide a Player", _ -> Universe.get().getPlayers().stream().map(PlayerRef::getUsername).toList())
            );
        }
    }

    @Component
    private static class GiveCommand extends SubCommand<ClansPlugin, EconomyCommand, PlayerRef> {

        public GiveCommand() {
            super("give", "Give Coins to a Player", Rank.ADMIN);
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

            final Integer amount = this.getModule().getManager().getAmount(args[1]);
            if (amount == null) {
                UtilMessage.message(playerRef, "Economy", "You did not input a valid Amount.");
                return;
            }

            if (amount <= 0) {
                UtilMessage.message(playerRef, "Economy", "Amount must be greater than Zero.");
                return;
            }

            UtilPlayer.searchPlayerRef(playerRef, args[0], true).ifPresent(targetPlayerRef -> {
                this.getModule().getManager().giveCoins(targetPlayerRef, amount);

                UtilMessage.message(playerRef, "Economy", "You gave <gold>%s</gold> to <yellow>%s</yellow>.".formatted(UtilString.formatToDollarByInteger(amount), targetPlayerRef.getUsername()));
                UtilMessage.message(targetPlayerRef, "Economy", "<yellow>%s</yellow> gave <gold>%s</gold> to you.".formatted(playerRef.getUsername(), UtilString.formatToDollarByInteger(amount)));
            });
        }

        @Override
        public List<Suggestion> getSuggestions() {
            return List.of(
                    new Suggestion("Player", "player", "Provide a Player", _ -> Universe.get().getPlayers().stream().map(PlayerRef::getUsername).toList())
            );
        }
    }

    @Component
    private static class TakeCommand extends SubCommand<ClansPlugin, EconomyCommand, PlayerRef> {

        public TakeCommand() {
            super("take", "Take Coins from a Player", Rank.ADMIN);
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

            final Integer amount = this.getModule().getManager().getAmount(args[1]);
            if (amount == null) {
                UtilMessage.message(playerRef, "Economy", "You did not input a valid Amount.");
                return;
            }

            if (amount <= 0) {
                UtilMessage.message(playerRef, "Economy", "Amount must be greater than Zero.");
                return;
            }

            UtilPlayer.searchPlayerRef(playerRef, args[0], true).ifPresent(targetPlayerRef -> {
                this.getModule().getManager().takeCoins(targetPlayerRef, amount);

                UtilMessage.message(playerRef, "Economy", "You took <gold>%s</gold> from <yellow>%s</yellow>.".formatted(UtilString.formatToDollarByInteger(amount), targetPlayerRef.getUsername()));
                UtilMessage.message(targetPlayerRef, "Economy", "<yellow>%s</yellow> took <gold>%s</gold> from you.".formatted(playerRef.getUsername(), UtilString.formatToDollarByInteger(amount)));
            });
        }

        @Override
        public List<Suggestion> getSuggestions() {
            return List.of(
                    new Suggestion("Player", "player", "Provide a Player", _ -> Universe.get().getPlayers().stream().map(PlayerRef::getUsername).toList())
            );
        }
    }

    @Component
    public static class SendCommand extends SubCommand<ClansPlugin, EconomyCommand, PlayerRef> {

        public SendCommand() {
            super("send", "Send Coins to a Player");

            this.addAliases("pay", "transfer");
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

            final Integer amount = this.getModule().getManager().getAmount(args[1]);
            if (amount == null) {
                UtilMessage.message(playerRef, "Economy", "You did not input a valid Amount.");
                return;
            }

            if (amount <= 0) {
                UtilMessage.message(playerRef, "Economy", "Amount must be greater than Zero.");
                return;
            }

            if (!(this.getModule().getManager().hasCoins(playerRef, amount))) {
                UtilMessage.message(playerRef, "Economy", "You have insufficient funds to send <gold>%s</gold>.".formatted(UtilString.formatToDollarByInteger(amount)));
                return;
            }

            UtilPlayer.searchPlayerRef(playerRef, args[0], true).ifPresent(targetPlayerRef -> {
                if (targetPlayerRef.equals(playerRef)) {
                    UtilMessage.message(playerRef, "Economy", "You cannot send money to yourself.");
                    return;
                }

                this.getModule().getManager().takeCoins(playerRef, amount);
                this.getModule().getManager().giveCoins(targetPlayerRef, amount);

                UtilMessage.message(playerRef, "Economy", "You sent <gold>%s</gold> to <yellow>%s</yellow>.".formatted(UtilString.formatToDollarByInteger(amount), targetPlayerRef.getUsername()));
                UtilMessage.message(targetPlayerRef, "Economy", "<yellow>%s</yellow> sent <gold>%s</gold> to you.".formatted(playerRef.getUsername(), UtilString.formatToDollarByInteger(amount)));
            });
        }

        @Override
        public List<Suggestion> getSuggestions() {
            return List.of(
                    new Suggestion("Player", "player", "Provide a Player", _ -> Universe.get().getPlayers().stream().map(PlayerRef::getUsername).toList())
            );
        }
    }
}