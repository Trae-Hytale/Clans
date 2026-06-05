package me.trae.clans.economy.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.command.suggestion.Suggestion;
import me.trae.clans.ClansPlugin;
import me.trae.clans.economy.EconomyManager;
import me.trae.core.command.Command;

import java.util.List;

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

    @Override
    public List<Suggestion> getSuggestions() {
        return List.of(
                new Suggestion("Player", "player", "Provide a Player", _ -> Universe.get().getPlayers().stream().map(PlayerRef::getUsername).toList())
        );
    }
}