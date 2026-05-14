package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.Alliance;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.data.enums.RelationRequestType;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.clan.ClanAllyEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;

@Component
public class AllyCommand extends AbstractClanSubCommand implements EventListener {

    public AllyCommand() {
        super("ally", "Ally a Clan");
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
            UtilMessage.message(playerRef, "Clans", "You did not input a Clan to Ally.");
            return;
        }

        this.getModule().getManager().searchClan(playerRef, args[0], true).ifPresent(targetClan -> {
            if (!(this.canAllyClan(playerRef, client, playerClan, targetClan))) {
                return;
            }

            UtilEvent.dispatch(new ClanAllyEvent(playerClan, playerRef, client, targetClan));
        });
    }

    private boolean canAllyClan(final PlayerRef playerRef, final Client client, final Clan playerClan, final Clan targetClan) {
        if (targetClan.equals(playerClan)) {
            UtilMessage.message(playerRef, "Clans", "You cannot request an alliance with yourself!");
            return false;
        }

        if (targetClan.isAllianceByClan(playerClan)) {
            UtilMessage.message(playerRef, "Clans", "You are already allies with %s!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)));
            return false;
        }

        if (!(targetClan.isNeutralByClan(playerClan))) {
            UtilMessage.message(playerRef, "Clans", "You must be neutral with %s to request an alliance!".formatted(this.getModule().getManager().getClanFullName(this.getModule().getManager().getClanRelationByClan(playerClan, targetClan), targetClan)));
            return false;
        }

        if (!(client.isAdministrating())) {
            if (targetClan.isAdmin()) {
                UtilMessage.message(playerRef, "Clans", "You cannot request an alliance with Admin Clans!");
                return false;
            }

            if (this.getModule().getManager().isSquadFull(playerClan)) {
                UtilMessage.message(playerRef, "Clans", "Your Clan has too many members/allies to request an alliance!");
                return false;
            }

            if (this.getModule().getManager().isSquadFull(targetClan)) {
                UtilMessage.message(playerRef, "Clans", "%s has too many members/allies to request an alliance!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)));
                return false;
            }

            if (playerClan.getRelationRequestByClan(targetClan, RelationRequestType.ALLIANCE).isPresent()) {
                UtilMessage.message(playerRef, "Clans", "You already requested an alliance with %s!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)));
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanAlly(final ClanAllyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final Clan targetClan = event.getTargetClan();

        if (event.getPlayerClient().isAdministrating()) {
            this.forceAlliance(clan, targetClan);
        } else if (targetClan.getRelationRequestByClan(clan, RelationRequestType.ALLIANCE).isEmpty()) {
            this.requestAlliance(playerRef, clan, targetClan);
        } else {
            this.acceptAlliance(playerRef, clan, targetClan);
        }
    }

    private void requestAlliance(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        playerClan.addRelationRequest(targetClan, RelationRequestType.ALLIANCE);
        this.getModule().getManager().getRepository().update(playerClan, ClanProperty.REQUESTS);

        UtilMessage.message(playerRef, "Clans", "You requested an alliance with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has requested an alliance with %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)), Collections.singletonList(playerRef.getUuid()));
        this.getModule().getManager().messageClan(targetClan, "Clans", "%s has requested an alliance with your Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, playerClan)), null);
    }

    private void acceptAlliance(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        this.handleAlliance(playerClan, targetClan);

        UtilMessage.message(playerRef, "Clans", "You accepted an alliance with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has accepted an alliance with %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)), Collections.singletonList(playerRef.getUuid()));
        this.getModule().getManager().messageClan(targetClan, "Clans", "%s has accepted an alliance with your Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, playerClan)), null);
    }

    private void forceAlliance(final Clan playerClan, final Clan targetClan) {
        this.handleAlliance(playerClan, targetClan);

        this.getModule().getManager().messageClan(playerClan, "Clans", "You are now allies with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)), null);
        this.getModule().getManager().messageClan(targetClan, "Clans", "You are now allies with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, playerClan)), null);
    }

    private void handleAlliance(final Clan playerClan, final Clan targetClan) {
        for (final RelationRequestType relationRequestType : RelationRequestType.values()) {
            playerClan.getRelationRequestByClan(targetClan, relationRequestType).ifPresent(request -> {
                playerClan.removeRelationRequest(targetClan, relationRequestType);
                this.getModule().getManager().getRepository().update(playerClan, ClanProperty.REQUESTS);
            });

            targetClan.getRelationRequestByClan(playerClan, relationRequestType).ifPresent(request -> {
                targetClan.removeRelationRequest(playerClan, relationRequestType);
                this.getModule().getManager().getRepository().update(targetClan, ClanProperty.REQUESTS);
            });
        }

        playerClan.addAlliance(new Alliance(targetClan));
        this.getModule().getManager().getRepository().update(playerClan, ClanProperty.ALLIANCES);

        targetClan.addAlliance(new Alliance(playerClan));
        this.getModule().getManager().getRepository().update(targetClan, ClanProperty.ALLIANCES);
    }
}