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
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.clan.ClanUnTrustEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;

@Component
public class UnTrustCommand extends AbstractClanSubCommand implements EventListener {

    public UnTrustCommand() {
        super("untrust", "Untrust a Clan");
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
            UtilMessage.message(playerRef, "Clans", "You did not input a Clan to Untrust.");
            return;
        }

        this.getModule().getManager().searchClan(playerRef, args[0], true).ifPresent(targetClan -> {
            if (!(this.canUnTrustClan(playerRef, playerClan, targetClan))) {
                return;
            }

            UtilEvent.dispatch(new ClanUnTrustEvent(playerClan, playerRef, client, targetClan));
        });
    }

    private boolean canUnTrustClan(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        if (!(targetClan.isTrustedAllianceByClan(playerClan))) {
            UtilMessage.message(playerRef, "Clans", "You are not trusted with %s!".formatted(this.getModule().getManager().getClanFullName(this.getModule().getManager().getClanRelationByClan(playerClan, targetClan), targetClan)));
            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanUnTrust(final ClanUnTrustEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.handleUnTrust(event.getPlayerRef(), event.getClan(), event.getTargetClan());
    }

    private void handleUnTrust(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        playerClan.getAllianceByClan(targetClan).ifPresent(alliance -> {
            alliance.setTrusted(false);
            this.getModule().getManager().getRepository().update(playerClan, ClanProperty.ALLIANCES);
        });

        targetClan.getAllianceByClan(playerClan).ifPresent(alliance -> {
            alliance.setTrusted(false);
            this.getModule().getManager().getRepository().update(targetClan, ClanProperty.ALLIANCES);
        });

        UtilMessage.message(playerRef, "Clans", "You revoked trust with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has revoked trust with %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, targetClan)), Collections.singletonList(playerRef.getUuid()));
        this.getModule().getManager().messageClan(targetClan, "Clans", "%s has revoked trust with your Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ALLIANCE, playerClan)), null);
    }
}