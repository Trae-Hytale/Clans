package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.ClanSubCommand;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.core.client.Client;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.UtilServer;

import javax.annotation.Nonnull;
import java.util.Optional;

@Component
public class DisbandCommand extends ClanSubCommand implements SubModule<Clans, ClanCommand> {

    public DisbandCommand() {
        super("disband", "Disband the Clan");
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world, final Clan playerClan) {
        final Optional<Member> memberOptional = playerClan.getMemberByPlayer(playerRef);
        if (memberOptional.isPresent()) {
            final Member member = memberOptional.get();

            if (!(member.hasRole(MemberRole.LEADER))) {
                UtilMessage.message(playerRef, "Clans", "You must be Clan Leader to Disband the Clan.");
                return;
            }
        }

        for (final PlayerRef target : UtilServer.getOnlinePlayers()) {
            final ClanRelation clanRelation = this.getModule().getManager().getClanRelationByClan(this.getModule().getManager().getClanByPlayer(target).orElse(null), playerClan);

            UtilMessage.message(target, "Clans", "%s has disbanded %s.".formatted(this.getModule().getManager().getPlayerName(clanRelation, playerRef), this.getModule().getManager().getClanFullName(clanRelation, playerClan)));
        }

        this.getModule().getManager().removeClan(playerClan);

        this.getModule().getManager().getRepository().deleteData(playerClan);
    }

    private boolean canDisbandClan(final PlayerRef player, final Client playerClient, final Clan playerClan) {
        return true;
    }
}