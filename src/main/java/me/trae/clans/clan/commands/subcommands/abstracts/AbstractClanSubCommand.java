package me.trae.clans.clan.commands.subcommands.abstracts;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.command.subcommand.PlayerSubCommand;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.commands.subcommands.abstracts.interfaces.IAbstractClanSubCommand;
import me.trae.clans.clan.data.Member;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;

import java.util.Optional;

public abstract class AbstractClanSubCommand extends PlayerSubCommand<ClansPlugin, ClanCommand> implements IAbstractClanSubCommand {

    public AbstractClanSubCommand(final String name, final String description) {
        super(name, description, Rank.DEFAULT);
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        final Optional<Clan> playerClanOptional = this.getModule().getManager().getClanByPlayer(playerRef);

        if (this.getRequiredState() == ClanStateRequirement.CLAN_PRESENT && playerClanOptional.isEmpty()) {
            ClanCommand.CLAN_EMPTY_MESSAGE_CONSUMER.accept(playerRef);
            return;
        }

        if (this.getRequiredState() == ClanStateRequirement.CLAN_EMPTY && playerClanOptional.isPresent()) {
            ClanCommand.CLAN_PRESENT_MESSAGE_CONSUMER.accept(playerRef);
            return;
        }

        final Optional<Client> clientOptional = this.getModule().getManager().getClientManager().getClientByPlayer(playerRef);

        if (!(clientOptional.map(Client::isAdministrating).orElse(false))) {
            if (this.isRequiredMemberRoleCheckOnExecute() && this.getRequiredMemberRole() != null) {
                if (playerClanOptional.isPresent()) {
                    final Clan playerClan = playerClanOptional.get();

                    final Optional<Member> memberOptional = playerClan.getMemberByPlayer(playerRef);
                    if (memberOptional.isPresent()) {
                        final Member member = memberOptional.get();

                        if (!(member.hasRole(this.getRequiredMemberRole()))) {
                            UtilMessage.message(playerRef, "Clans", "You must be clan <white>%s</white> to execute this command!".formatted(this.getRequiredMemberRole().getName()));
                            return;
                        }
                    }
                }
            }
        }

        UtilPlayer.getPlayer(playerRef).ifPresent(player -> {
            this.execute(playerRef, player, clientOptional.orElse(null), playerClanOptional.orElse(null), args);
        });
    }
}