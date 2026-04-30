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
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.data.enums.RelationRequestType;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanNeutralEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;

@Component
public class NeutralCommand extends AbstractClanSubCommand implements Listener {

    public NeutralCommand() {
        super("neutral", "Neutral a Clan");
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
            UtilMessage.message(playerRef, "Clans", "You did not input a Clan to Neutral.");
            return;
        }

        this.getModule().getManager().searchClan(player, args[0], true).ifPresent(targetClan -> {
            if (!(this.canNeutralClan(playerRef, client, playerClan, targetClan))) {
                return;
            }

            UtilEvent.dispatch(new ClanNeutralEvent(playerClan, playerRef, client, targetClan));
        });
    }

    private boolean canNeutralClan(final PlayerRef playerRef, final Client client, final Clan playerClan, final Clan targetClan) {
        if (targetClan.equals(playerClan)) {
            UtilMessage.message(playerRef, "Clans", "You cannot request neutrality with yourself!");
            return false;
        }

        if (targetClan.isNeutralByClan(playerClan)) {
            UtilMessage.message(playerRef, "Clans", "You are already neutral with %s!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)));
            return false;
        }

        if (!(client.isAdministrating())) {
            if (targetClan.isAdmin()) {
                UtilMessage.message(playerRef, "Clans", "You cannot request neutrality with Admin Clans!");
                return false;
            }

            if (playerClan.getRelationRequestByClan(targetClan, RelationRequestType.NEUTRAL).isPresent()) {
                UtilMessage.message(playerRef, "Clans", "You already requested neutrality with %s!".formatted(this.getModule().getManager().getClanFullName(this.getModule().getManager().getClanRelationByClan(playerClan, targetClan), targetClan)));
                return false;
            }

            if (targetClan.isPillageByClan(playerClan)) {
                UtilMessage.message(playerRef, "Clans", "You cannot neutral a clan you are being conquered by!");
                return false;
            }

            if (playerClan.isPillageByClan(targetClan)) {
                UtilMessage.message(playerRef, "Clans", "You cannot neutral a clan you are conquering!");
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanNeutral(final ClanNeutralEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final Clan targetClan = event.getTargetClan();

        if (event.getPlayerClient().isAdministrating()) {
            this.forceNeutrality(clan, targetClan);
        } else if (targetClan.getRelationRequestByClan(clan, RelationRequestType.NEUTRAL).isEmpty()) {
            this.requestNeutrality(playerRef, clan, targetClan);
        } else {
            this.acceptNeutrality(playerRef, clan, targetClan);
        }
    }

    private void requestNeutrality(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        playerClan.addRelationRequest(targetClan, RelationRequestType.NEUTRAL);
        this.getModule().getManager().getRepository().update(playerClan, ClanProperty.REQUESTS);

        UtilMessage.message(playerRef, "Clans", "You requested neutrality with %s.".formatted(this.getModule().getManager().getClanFullName(this.getModule().getManager().getClanRelationByClan(playerClan, targetClan), targetClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has requested neutrality with %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanFullName(this.getModule().getManager().getClanRelationByClan(playerClan, targetClan), targetClan)), Collections.singletonList(playerRef.getUuid()));
        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has requested neutrality with your Clan.".formatted(this.getModule().getManager().getClanFullName(this.getModule().getManager().getClanRelationByClan(targetClan, playerClan), playerClan)), null);
    }

    private void acceptNeutrality(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        this.handleNeutral(playerClan, targetClan);

        UtilMessage.message(playerRef, "Clans", "You accepted neutrality with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has accepted neutrality with %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)), Collections.singletonList(playerRef.getUuid()));
        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has accepted neutrality with your Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, playerClan)), null);
    }

    private void forceNeutrality(final Clan playerClan, final Clan targetClan) {
        this.handleNeutral(playerClan, targetClan);

        this.getModule().getManager().messageClan(playerClan, "Clans", "You are now neutral with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, targetClan)), null);
        this.getModule().getManager().messageClan(playerClan, "Clans", "You are now neutral with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.NEUTRAL, playerClan)), null);
    }

    private void handleNeutral(final Clan playerClan, final Clan targetClan) {
        playerClan.getRelationRequestByClan(targetClan, RelationRequestType.NEUTRAL).ifPresent(request -> {
            playerClan.removeRelationRequest(targetClan, RelationRequestType.NEUTRAL);
            this.getModule().getManager().getRepository().update(playerClan, ClanProperty.REQUESTS);
        });

        targetClan.getRelationRequestByClan(playerClan, RelationRequestType.NEUTRAL).ifPresent(request -> {
            targetClan.removeRelationRequest(playerClan, RelationRequestType.NEUTRAL);
            this.getModule().getManager().getRepository().update(targetClan, ClanProperty.REQUESTS);
        });

        playerClan.getAllianceByClan(targetClan).ifPresent(alliance -> {
            playerClan.removeAlliance(alliance);
            this.getModule().getManager().getRepository().update(playerClan, ClanProperty.ALLIANCES);
        });

        targetClan.getAllianceByClan(playerClan).ifPresent(alliance -> {
            targetClan.removeAlliance(alliance);
            this.getModule().getManager().getRepository().update(targetClan, ClanProperty.ALLIANCES);
        });

        playerClan.getEnemyByClan(targetClan).ifPresent(enemy -> {
            playerClan.removeEnemy(enemy);
            this.getModule().getManager().getRepository().update(playerClan, ClanProperty.ENEMIES);
        });

        targetClan.getEnemyByClan(playerClan).ifPresent(enemy -> {
            targetClan.removeEnemy(enemy);
            this.getModule().getManager().getRepository().update(targetClan, ClanProperty.ENEMIES);
        });

        playerClan.getPillageByClan(targetClan).ifPresent(pillage -> {
            playerClan.removePillage(pillage);
            this.getModule().getManager().getRepository().update(playerClan, ClanProperty.PILLAGES);
        });

        targetClan.getPillageByClan(playerClan).ifPresent(pillage -> {
            targetClan.removePillage(pillage);
            this.getModule().getManager().getRepository().update(targetClan, ClanProperty.PILLAGES);
        });
    }
}