package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.ClanSubCommand;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanDisbandEvent;
import me.trae.core.client.Client;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.event.Listener;
import me.trae.framework.event.annotations.EventHandler;
import me.trae.framework.event.constants.EventPriority;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.UtilServer;

@Component
public class DisbandCommand extends ClanSubCommand implements SubModule<Clans, ClanCommand>, Listener {

    public DisbandCommand() {
        super("disband", "Disband the Clan");
    }

    @Override
    protected MemberRole getRequiredMemberRole() {
        return MemberRole.LEADER;
    }

    @Override
    protected void execute(final PlayerRef player, final Client client, final Clan playerClan, final String[] args, final CommandContext commandContext) {
        UtilServer.callEvent(new ClanDisbandEvent(playerClan, player, client));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreEvent(final ClanDisbandEvent event) {
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPostEvent(final ClanDisbandEvent event) {
        final Clan clan = event.getClan();
        final PlayerRef player = event.getPlayer();

        for (final PlayerRef target : UtilServer.getOnlinePlayers()) {
            final ClanRelation clanRelation = this.getModule().getManager().getClanRelationByPlayer(target, player);

            UtilMessage.message(target, "Clans", "%s has disbanded %s.".formatted(this.getModule().getManager().getPlayerName(clanRelation, player), this.getModule().getManager().getClanFullName(clanRelation, clan)));
        }

        this.getModule().getManager().disbandClan(clan);
    }
}