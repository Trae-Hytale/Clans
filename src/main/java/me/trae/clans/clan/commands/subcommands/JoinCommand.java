package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanJoinEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Component
public class JoinCommand extends AbstractClanSubCommand implements Listener {

    public JoinCommand() {
        super("join", "Join a Clan");
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_EMPTY;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (args.length == 0) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Clan to Join.");
            return;
        }

        final String targetClanName = args[0];

        final Optional<Clan> targetClanOptional = this.getModule().getManager().getClanByName(targetClanName);
        if (targetClanOptional.isEmpty()) {
            UtilMessage.message(player, "Clans", "Could not find Clan <yellow>%s</yellow>.".formatted(targetClanName));
            return;
        }

        final Clan targetClan = targetClanOptional.get();

        if (!(this.canJoinClan(playerRef, client, targetClan))) {
            return;
        }

        UtilEvent.dispatch(new ClanJoinEvent(targetClan, playerRef, client));
    }

    private boolean canJoinClan(final PlayerRef playerRef, final Client client, final Clan targetClan) {
        if (!(client.isAdministrating())) {
            if (targetClan.isAdmin()) {
                UtilMessage.message(playerRef, "Clans", "You cannot join Admin Clans!");
                return false;
            }

            if (targetClan.getInvitationRequestByPlayer(playerRef).isEmpty()) {
                UtilMessage.message(playerRef, "Clans", "You have not been invited to join %s!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)));

                if (this.getModule().getManager().getCooldownManager().attempt(playerRef, "CLAN_JOIN_ATTEMPT:%s".formatted(targetClan.getName()), Duration.ofMinutes(5).toMillis(), false)) {
                    this.getModule().getManager().messageClan(targetClan, "Clans", "%s tried to join the clan, but is not invited.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, playerRef)), null);
                }
                return false;
            }

            if (this.getModule().getManager().isSquadFull(targetClan)) {
                UtilMessage.message(playerRef, "Clans", "%s has too many members/allies to join!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)));
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanJoin(final ClanJoinEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();

        clan.removeInvitationRequest(playerRef);
        this.getModule().getManager().getRepository().update(clan, ClanProperty.REQUESTS);

        clan.addMember(new Member(playerRef, event.getPlayerClient().isAdministrating() ? MemberRole.LEADER : MemberRole.RECRUIT));
        this.getModule().getManager().getRepository().update(clan, ClanProperty.MEMBERS);

        UtilMessage.message(playerRef, "Clans", "You joined %s.".formatted(this.getModule().getManager().getClanShortName(ClanRelation.SELF, clan)));

        this.getModule().getManager().messageClan(clan, "Clans", "%s has joined the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef)), Collections.singletonList(playerRef.getUuid()));
    }
}