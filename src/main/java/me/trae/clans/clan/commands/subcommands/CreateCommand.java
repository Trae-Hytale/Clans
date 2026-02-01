package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.Clans;
import me.trae.clans.clan.AdminClan;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.ClanSubCommand;
import me.trae.clans.clan.commands.subcommands.enums.ClanConditionType;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanCreateEvent;
import me.trae.core.client.Client;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.event.Listener;
import me.trae.framework.event.annotations.EventHandler;
import me.trae.framework.event.constants.EventPriority;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.UtilServer;

@Component
public class CreateCommand extends ClanSubCommand implements SubModule<Clans, ClanCommand>, Listener {

    protected CreateCommand() {
        super("create", "Create a Clan");
    }

    @Override
    protected ClanConditionType getClanConditionType() {
        return ClanConditionType.ABSENT;
    }

    @Override
    protected void execute(final PlayerRef player, final Client client, final Clan playerClan, final String[] args, final CommandContext commandContext) {
        if (args.length == 0) {
            UtilMessage.message(player, "Clans", "You did not input a Name to Create.");
            return;
        }

        final String clanName = args[0];

        final Clan clan = client.isAdministrating() ? new AdminClan(player, clanName) : new Clan(player, clanName);

        UtilServer.callEvent(new ClanCreateEvent(clan, player, client));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreEvent(final ClanCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final String clanName = event.getClan().getName();

        final PlayerRef player = event.getPlayer();

        if (this.getModule().getSubCommands().containsKey(clanName.toLowerCase())) {
            event.setCancelled(true);
            UtilMessage.message(player, "Clans", "You cannot use that name!");
            return;
        }

        if (this.getModule().getManager().getClanByName(clanName).isPresent()) {
            event.setCancelled(true);
            UtilMessage.message(player, "Clans", "That name is already used by another clan!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPostEvent(final ClanCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final PlayerRef player = event.getPlayer();

        final Clan clan = event.getClan();

        this.getModule().getManager().addClan(clan);
        this.getModule().getManager().getRepository().saveData(clan);

        for (final PlayerRef target : UtilServer.getOnlinePlayers()) {
            final ClanRelation clanRelation = this.getModule().getManager().getClanRelationByPlayer(target, player);

            UtilMessage.message(target, "Clans", "%s has formed %s.".formatted(this.getModule().getManager().getPlayerName(clanRelation, player), this.getModule().getManager().getClanFullName(clanRelation, clan)));
        }
    }
}