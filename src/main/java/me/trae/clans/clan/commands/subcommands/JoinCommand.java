package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.ClanSubCommand;
import me.trae.clans.clan.commands.subcommands.enums.ClanConditionType;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanProperty;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanJoinEvent;
import me.trae.core.client.Client;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.event.Listener;
import me.trae.framework.event.annotations.EventHandler;
import me.trae.framework.event.constants.EventPriority;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.UtilServer;

import java.util.List;

@Component
public class JoinCommand extends ClanSubCommand implements SubModule<Clans, ClanCommand>, Listener {

    public JoinCommand() {
        super("join", "Join a Clan");
    }

    @Override
    protected ClanConditionType getClanConditionType() {
        return ClanConditionType.ABSENT;
    }

    @Override
    protected void execute(final PlayerRef player, final Client client, final Clan playerClan, final String[] args, final CommandContext commandContext) {
        if (args.length == 0) {
            UtilMessage.message(player, "Clans", "You did not input a Clan to Join.");
            return;
        }

        this.getModule().getManager().searchClan(player, args[0], true).ifPresent(clan -> {
            UtilServer.callEvent(new ClanJoinEvent(clan, player, client));
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreEvent(final ClanJoinEvent event) {
        final Clan clan = event.getClan();
        final PlayerRef player = event.getPlayer();

        if (!(clan.isInvitationRequest(player))) {
            event.setCancelled(true);
            UtilMessage.message(player, "Clans", "You have not been invited to join %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, clan)));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPostEvent(final ClanJoinEvent event) {
        final Clan clan = event.getClan();
        final PlayerRef player = event.getPlayer();

        clan.removeInvitationRequest(player);

        clan.addMember(new Member(player, event.getPlayerClient().isAdministrating() ? MemberRole.LEADER : MemberRole.RECRUIT));
        this.getModule().getManager().getRepository().updateData(clan, ClanProperty.MEMBERS);

        UtilMessage.message(player, "Clans", "You joined %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.SELF, clan)));

        this.getModule().getManager().messageClan(clan, "Clans", "%s has joined the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, player)), List.of(player.getUuid()));
    }
}