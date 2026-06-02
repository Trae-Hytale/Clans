package me.trae.clans.economy.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import me.trae.clans.ClansPlugin;
import me.trae.clans.economy.EconomyManager;
import me.trae.core.command.Command;

@Component
public class PayCommand extends Command<ClansPlugin, EconomyManager, PlayerRef> {

    private final EconomyCommand.SendCommand sendCommand;

    public PayCommand(final EconomyCommand.SendCommand sendCommand) {
        super("pay", "Send Coins to a Player");

        this.sendCommand = sendCommand;

        this.addAliases("sendmoney", "sendcoins");
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        this.sendCommand.execute(playerRef, args);
    }
}