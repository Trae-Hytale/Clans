package me.trae.clans.economy.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.command.PlayerCommand;
import me.trae.clans.ClansPlugin;
import me.trae.clans.economy.EconomyManager;
import me.trae.core.client.enums.Rank;

@Component
public class PayCommand extends PlayerCommand<ClansPlugin, EconomyManager> {

    private final EconomyCommand.SendCommand sendCommand;

    public PayCommand(final EconomyCommand.SendCommand sendCommand) {
        super("pay", "Send Coins to a Player", Rank.DEFAULT);

        this.sendCommand = sendCommand;

        this.addAliases("sendmoney", "sendcoins");
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        this.sendCommand.execute(playerRef, args);
    }
}