package me.trae.clans.clan.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.command.PlayerCommand;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.core.client.enums.Rank;

import java.util.Optional;

@Component
public class ClanCommand extends PlayerCommand<ClansPlugin, ClanManager> {

    public ClanCommand() {
        super("clan", "Clan management", Rank.DEFAULT);

        this.addAliases("c");
        this.addAliases("faction", "fac", "f");
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

        if (args.length == 0) {
            if (playerClanOptional.isEmpty()) {
                UtilMessage.message(playerRef, "Clans", "You are not in a Clan.");
                return;
            }

            final Clan playerClan = playerClanOptional.get();

            this.getManager().showClanInformation(playerRef, playerClan, playerClan);
            return;
        }

        if (args.length == 1) {
            final String searchName = args[0];

            this.getManager().getClanByName(searchName).ifPresentOrElse(targetClan -> {
                this.getManager().showClanInformation(playerRef, playerClanOptional.orElse(null), targetClan);
            }, () -> {
                UtilMessage.message(playerRef, "Clans", "Could not find Clan <yellow>%s</yellow>.".formatted(searchName));
            });
        }
    }
}