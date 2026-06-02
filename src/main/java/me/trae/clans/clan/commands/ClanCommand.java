package me.trae.clans.clan.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.core.client.Client;
import me.trae.core.command.Command;

import java.util.Optional;
import java.util.function.Consumer;

@Component
public class ClanCommand extends Command<ClansPlugin, ClanManager, PlayerRef> {

    public static final Consumer<PlayerRef> CLAN_EMPTY_MESSAGE_CONSUMER = playerRef -> UtilMessage.message(playerRef, "Clans", "You are not in a Clan.");
    public static final Consumer<PlayerRef> CLAN_PRESENT_MESSAGE_CONSUMER = playerRef -> UtilMessage.message(playerRef, "Clans", "You are already in a Clan.");

    public ClanCommand() {
        super("clan", "Clan management");

        this.addAliases("c");
        this.addAliases("faction", "fac", "f");
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        final Optional<Client> clientOptional = this.getManager().getClientManager().getClientByPlayer(playerRef);
        if (clientOptional.isEmpty()) {
            return;
        }

        final Client client = clientOptional.get();

        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

        if (args.length == 0) {
            if (playerClanOptional.isEmpty()) {
                CLAN_EMPTY_MESSAGE_CONSUMER.accept(playerRef);
                return;
            }

            final Clan playerClan = playerClanOptional.get();

            this.getManager().showClanInformation(playerRef, client, playerClan, playerClan);
            return;
        }

        if (args.length == 1) {
            this.getManager().searchClan(playerRef, args[0], true).ifPresent(targetClan -> {
                if (targetClan.isAdmin() && !(client.isAdministrating())) {
                    UtilMessage.message(playerRef, "Clans", "You cannot view Admin Clans!");
                    return;
                }

                this.getManager().showClanInformation(playerRef, client, playerClanOptional.orElse(null), targetClan);
            });
        }
    }
}