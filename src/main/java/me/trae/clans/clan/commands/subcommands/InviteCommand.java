package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.ClanSubCommand;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanInviteEvent;
import me.trae.core.client.Client;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.event.Listener;
import me.trae.framework.event.annotations.EventHandler;
import me.trae.framework.event.constants.EventPriority;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.UtilPlayer;
import me.trae.framework.utility.UtilServer;

import java.util.List;
import java.util.Optional;

@Component
public class InviteCommand extends ClanSubCommand implements SubModule<Clans, ClanCommand>, Listener {

    public InviteCommand() {
        super("invite", "Invite a Player");
    }

    @Override
    protected void execute(final PlayerRef player, final Client client, final Clan playerClan, final String[] args, final CommandContext commandContext) {
        if (args.length == 0) {
            UtilMessage.message(player, "Clans", "You did not input a Player to Invite.");
            return;
        }

        UtilPlayer.searchPlayer(player, args[0], true).ifPresent(targetPlayer -> {
            UtilServer.callEvent(new ClanInviteEvent(playerClan, player, client, targetPlayer));
        });
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreEvent(final ClanInviteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan playerClan = event.getClan();
        final PlayerRef player = event.getPlayer();
        final PlayerRef targetPlayer = event.getTargetPlayer();

        if (targetPlayer.equals(player)) {
            UtilMessage.message(player, "Clans", "You cannot invite yourself.");
            return;
        }

        final Optional<Clan> targetPlayerClanOptional = this.getModule().getManager().getClanByPlayer(targetPlayer);
        if (targetPlayerClanOptional.isPresent()) {
            final Clan targetPlayerClan = targetPlayerClanOptional.get();

            event.setCancelled(true);

            if (targetPlayerClan.equals(playerClan)) {
                UtilMessage.message(player, "Clans", "%s is already apart of the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, targetPlayer)));
            } else {
                final ClanRelation clanRelation = this.getModule().getManager().getClanRelationByPlayer(player, targetPlayer);

                UtilMessage.message(player, "Clans", "%s is already apart of %s.".formatted(this.getModule().getManager().getPlayerName(clanRelation, targetPlayer), this.getModule().getManager().getClanFullName(clanRelation, targetPlayerClanOptional.get())));
            }
            return;
        }

        if (playerClan.isInvitationRequest(targetPlayer)) {
            event.setCancelled(true);
            UtilMessage.message(player, "Clans", "Your Clan has already invited %s to join the clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, targetPlayer)));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPostEvent(final ClanInviteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan playerClan = event.getClan();
        final PlayerRef player = event.getPlayer();
        final PlayerRef targetPlayer = event.getTargetPlayer();

        playerClan.addInvitationRequest(targetPlayer);

        UtilMessage.message(player, "Clans", "You have invited %s to join the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, targetPlayer)));

        UtilMessage.message(targetPlayer, "Clans", "%s has invited you to join %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, player), this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, playerClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has invited %s to join the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, player), this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, targetPlayer)), List.of(player.getUuid()));
    }
}