package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.clan.ClanInviteEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;
import java.util.Optional;

@Component
public class InviteCommand extends AbstractClanSubCommand implements EventListener {

    public InviteCommand() {
        super("invite", "Invite a Player to the Clan");
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_PRESENT;
    }

    @Override
    public MemberRole getRequiredMemberRole() {
        return MemberRole.ADMIN;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (args.length == 0) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Player to Invite.");
            return;
        }

        UtilPlayer.searchPlayerRef(playerRef, args[0], true).ifPresent(targetPlayerRef -> {
            if (!(this.canInvitePlayer(playerRef, client, playerClan, targetPlayerRef))) {
                return;
            }

            UtilEvent.dispatch(new ClanInviteEvent(playerClan, playerRef, targetPlayerRef));
        });
    }

    private boolean canInvitePlayer(final PlayerRef playerRef, final Client client, final Clan playerClan, final PlayerRef targetPlayerRef) {
        if (targetPlayerRef.equals(playerRef)) {
            UtilMessage.message(playerRef, "Clans", "You cannot invite yourself!");
            return false;
        }

        final Optional<Clan> targetPlayerClanOptional = this.getModule().getManager().getClanByPlayer(targetPlayerRef);
        if (targetPlayerClanOptional.isPresent()) {
            final Clan targetPlayerClan = targetPlayerClanOptional.get();

            if (targetPlayerClan.equals(playerClan)) {
                UtilMessage.message(playerRef, "Clans", "%s is already in your Clan!".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, targetPlayerRef)));
                return false;
            }

            final ClanRelation clanRelation = this.getModule().getManager().getClanRelationByClan(playerClan, targetPlayerClan);

            UtilMessage.message(playerRef, "Clans", "%s is already in of %s!".formatted(this.getModule().getManager().getPlayerName(clanRelation, targetPlayerRef), this.getModule().getManager().getClanFullName(clanRelation, targetPlayerClan)));
            return false;
        }

        if (!(client.isAdministrating())) {
            if (playerClan.getInvitationRequestByPlayer(targetPlayerRef).isPresent()) {
                UtilMessage.message(playerRef, "Clans", "You have already invited %s to join the Clan!".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, targetPlayerRef)));
                return false;
            }

            if (this.getModule().getManager().isSquadFull(playerClan)) {
                UtilMessage.message(playerRef, "Clans", "Your Clan has too many members/allies to invite players!");
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanInvite(final ClanInviteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final PlayerRef targetPlayerRef = event.getTargetPlayerRef();

        clan.addInvitationRequest(targetPlayerRef);
        this.getModule().getManager().getRepository().update(clan, ClanProperty.REQUESTS);

        UtilMessage.message(playerRef, "Clans", "You have invited %s to join the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, targetPlayerRef)));
        UtilMessage.message(targetPlayerRef, "Clans", "%s has invited you to join %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, playerRef), this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, clan)));

        this.getModule().getManager().messageClan(clan, "Clans", "%s has invited %s to join the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, targetPlayerRef)), Collections.singletonList(playerRef.getUuid()));
    }
}