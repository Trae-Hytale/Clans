package me.trae.clans.clan.commands.subcommands.abstracts;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.enums.ClanConditionType;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.types.subcommand.player.PlayerSubCommand;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.utility.UtilColor;
import me.trae.framework.utility.UtilMessage;

import java.util.Optional;

public abstract class ClanSubCommand extends PlayerSubCommand implements SubModule<Clans, ClanCommand> {

    public ClanSubCommand(final String name, final String description, final Rank requiredRank) {
        super(name, description, requiredRank);
    }

    public ClanSubCommand(final String name, final String description) {
        this(name, description, Rank.DEFAULT);
    }

    @Override
    protected void onExecute(final PlayerRef player, final Client client, final String[] args, final CommandContext commandContext) {
        final Optional<Clan> playerClanOptional = this.getModule().getManager().getClanByPlayer(player);

        switch (this.getClanConditionType()) {
            case PRESENT -> {
                if (playerClanOptional.isEmpty()) {
                    this.getModule().sendAbsentClanMessage(player);
                    return;
                }
                break;
            }
            case ABSENT -> {
                if (playerClanOptional.isPresent()) {
                    this.getModule().sendPresentClanMessage(player);
                    return;
                }
                break;
            }
        }

        final MemberRole requiredMemberRole = this.getRequiredMemberRole();

        if (requiredMemberRole != null && !(client.isAdministrating())) {
            if (playerClanOptional.isPresent()) {
                final Optional<Member> memberOptional = playerClanOptional.get().getMemberByPlayer(player);

                if (memberOptional.isPresent()) {
                    if (!(memberOptional.get().hasRole(requiredMemberRole))) {
                        UtilMessage.message(player, "Clans", "You must be Clan %s to %s".formatted(UtilColor.serialize(requiredMemberRole.getColor(), requiredMemberRole.getName()), this.getDescription()));
                        return;
                    }
                }
            }
        }

        this.execute(player, client, playerClanOptional.orElse(null), args, commandContext);
    }

    protected ClanConditionType getClanConditionType() {
        return ClanConditionType.PRESENT;
    }

    protected MemberRole getRequiredMemberRole() {
        return null;
    }

    protected abstract void execute(final PlayerRef player, final Client client, final Clan playerClan, final String[] args, final CommandContext commandContext);
}