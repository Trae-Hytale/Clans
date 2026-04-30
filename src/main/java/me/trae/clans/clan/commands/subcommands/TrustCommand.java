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
import me.trae.clans.clan.events.ClanTrustEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;

@Component
public class TrustCommand extends AbstractClanSubCommand implements Listener {

    public TrustCommand() {
        super("trust", "Trust a Clan");
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
            UtilMessage.message(playerRef, "Clans", "You did not input a Clan to Trust.");
            return;
        }

        this.getModule().getManager().searchClan(player, args[0], true).ifPresent(targetClan -> {
            if (!(this.canTrustClan(playerRef, client, playerClan, targetClan))) {
                return;
            }

            UtilEvent.dispatch(new ClanTrustEvent(playerClan, playerRef, client, targetClan));
        });
    }

    private boolean canTrustClan(final PlayerRef playerRef, final Client client, final Clan playerClan, final Clan targetClan) {
        if (targetClan.equals(playerClan)) {
            UtilMessage.message(playerRef, "Clans", "You cannot request to trust with yourself!");
            return false;
        }

        if (!(targetClan.isAllianceByClan(playerClan))) {
            UtilMessage.message(playerRef, "Clans", "You are not allies with %s!".formatted(this.getModule().getManager().getClanFullName(this.getModule().getManager().getClanRelationByClan(playerClan, targetClan), targetClan)));
            return false;
        }

        if (targetClan.isTrustedAllianceByClan(playerClan)) {
            UtilMessage.message(playerRef, "Clans", "You are already trusted with %s!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.TRUSTED_ALLIANCE, targetClan)));
            return false;
        }

        if (!(client.isAdministrating())) {
            if (targetClan.isAdmin()) {
                UtilMessage.message(playerRef, "Clans", "You cannot request to trust with Admin Clans!");
                return false;
            }

            if (playerClan.getRelationRequestByClan(targetClan, RelationRequestType.TRUST).isPresent()) {
                UtilMessage.message(playerRef, "Clans", "You already requested to trust with %s!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)));
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanTrust(final ClanTrustEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final Clan targetClan = event.getTargetClan();

        if (event.getPlayerClient().isAdministrating()) {
            this.forceTrust(clan, targetClan);
        } else if (targetClan.getRelationRequestByClan(clan, RelationRequestType.TRUST).isEmpty()) {
            this.requestTrust(playerRef, clan, targetClan);
        } else {
            this.acceptTrust(playerRef, clan, targetClan);
        }
    }

    private void requestTrust(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        playerClan.addRelationRequest(targetClan, RelationRequestType.TRUST);
        this.getModule().getManager().getRepository().update(playerClan, ClanProperty.REQUESTS);

        UtilMessage.message(playerRef, "Clans", "You requested to trust with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has requested to trust with %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)), Collections.singletonList(playerRef.getUuid()));
        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has requested to trust with your Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, playerClan)), null);
    }

    private void acceptTrust(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        this.handleTrust(playerClan, targetClan);

        UtilMessage.message(playerRef, "Clans", "You accepted to trust with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.TRUSTED_ALLIANCE, targetClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has accepted to trust with %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanFullName(ClanRelation.TRUSTED_ALLIANCE, targetClan)), Collections.singletonList(playerRef.getUuid()));
        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has accepted to trust with your Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.TRUSTED_ALLIANCE, playerClan)), null);
    }

    private void forceTrust(final Clan playerClan, final Clan targetClan) {
        this.handleTrust(playerClan, targetClan);

        this.getModule().getManager().messageClan(playerClan, "Clans", "You are now trusted with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.TRUSTED_ALLIANCE, targetClan)), null);
        this.getModule().getManager().messageClan(playerClan, "Clans", "You are now trusted with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.TRUSTED_ALLIANCE, playerClan)), null);
    }

    private void handleTrust(final Clan playerClan, final Clan targetClan) {
        playerClan.getRelationRequestByClan(targetClan, RelationRequestType.TRUST).ifPresent(request -> {
            playerClan.removeRelationRequest(targetClan, RelationRequestType.TRUST);
            this.getModule().getManager().getRepository().update(playerClan, ClanProperty.REQUESTS);
        });

        targetClan.getRelationRequestByClan(playerClan, RelationRequestType.TRUST).ifPresent(request -> {
            targetClan.removeRelationRequest(playerClan, RelationRequestType.TRUST);
            this.getModule().getManager().getRepository().update(targetClan, ClanProperty.REQUESTS);
        });

        playerClan.getAllianceByClan(targetClan).ifPresent(alliance -> {
            alliance.setTrusted(true);
            this.getModule().getManager().getRepository().update(playerClan, ClanProperty.ALLIANCES);
        });

        targetClan.getAllianceByClan(playerClan).ifPresent(alliance -> {
            alliance.setTrusted(true);
            this.getModule().getManager().getRepository().update(targetClan, ClanProperty.ALLIANCES);
        });
    }
}