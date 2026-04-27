package me.trae.clans.clan.commands.subcommands.abstracts;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.command.subcommand.PlayerSubCommand;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.commands.subcommands.abstracts.interfaces.IAbstractClanSubCommand;
import me.trae.clans.clan.data.Member;

import java.util.Optional;

public abstract class AbstractClanSubCommand extends PlayerSubCommand<ClansPlugin, ClanCommand> implements IAbstractClanSubCommand {

    public AbstractClanSubCommand(final String name, final String description) {
        super(name, description);
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        final Optional<Clan> playerClanOptional = this.getModule().getManager().getClanByPlayer(playerRef);

        if (this.getRequiredState() == ClanStateRequirement.CLAN_PRESENT && playerClanOptional.isEmpty()) {
            UtilMessage.message(playerRef, "Clans", "You are not in a Clan.");
            return;
        }

        if (this.getRequiredState() == ClanStateRequirement.CLAN_EMPTY && playerClanOptional.isPresent()) {
            UtilMessage.message(playerRef, "Clans", "You are already in a Clan.");
            return;
        }

        if (this.getRequiredMemberRole() != null) {
            if (playerClanOptional.isPresent()) {
                final Clan playerClan = playerClanOptional.get();

                final Optional<Member> memberOptional = playerClan.getMemberByPlayer(playerRef);
                if (memberOptional.isPresent()) {
                    final Member member = memberOptional.get();

                    if (!(member.hasRole(this.getRequiredMemberRole()))) {
                        UtilMessage.message(playerRef, "Clans", "You must be <white>Clan %s</white> to execute this!".formatted(this.getRequiredMemberRole().getName()));
                        return;
                    }
                }
            }
        }

        this.execute(playerRef, playerClanOptional.orElse(null), args);
    }
}