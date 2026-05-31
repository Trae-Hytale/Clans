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
import me.trae.clans.clan.events.member.MemberPromoteEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;

import java.util.List;

@Component
public class PromoteCommand extends AbstractClanSubCommand implements EventListener {

    public PromoteCommand() {
        super("promote", "Promote a Member");
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_PRESENT;
    }

    @Override
    public MemberRole getRequiredMemberRole() {
        return MemberRole.LEADER;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (args.length == 0) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Member to Promote.");
            return;
        }

        this.getModule().getManager().searchMemberClient(playerClan, playerRef, args[0], true).ifPresent(targetClient -> {
            if (!(this.canPromoteMember(playerRef, client, playerClan, targetClient))) {
                return;
            }

            UtilEvent.dispatch(new MemberPromoteEvent(playerClan, playerRef, targetClient));
        });
    }

    private boolean canPromoteMember(final PlayerRef playerRef, final Client client, final Clan playerClan, final Client targetClient) {
        if (!(client.isAdministrating())) {
            if (targetClient.equals(client)) {
                UtilMessage.message(playerRef, "Clans", "You cannot promote yourself!");
                return false;
            }

            if (targetClient.isAdministrating() && !(client.hasRank(Rank.OWNER))) {
                UtilMessage.message(playerRef, "Clans", "You do not outrank %s!".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, targetClient.getName())));
                return false;
            }
        }

        if (playerClan.getMemberById(targetClient.getId()).map(member -> member.getRole().isHighestValue()).orElse(false)) {
            UtilMessage.message(playerRef, "Clans", "%s cannot be promoted any further!".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, targetClient.getName())));
            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMemberPromote(final MemberPromoteEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final Client targetClient = event.getTargetClient();

        clan.getMemberById(targetClient.getId()).ifPresent(member -> {
            MemberRole.getByOrdinal(member.getRole().ordinal() + 1).ifPresent(memberRole -> {
                member.setRole(memberRole);
                this.getModule().getManager().getRepository().update(clan, ClanProperty.MEMBERS);

                if (targetClient.getId().equals(playerRef.getUuid())) {
                    UtilMessage.message(playerRef, "Clans", "You promoted yourself to <green>%s</green>.".formatted(memberRole.getName()));
                } else {
                    UtilMessage.message(playerRef, "Clans", "You have promoted %s to <green>%s</green>.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, targetClient.getName()), memberRole.getName()));
                    UtilMessage.message(targetClient.getPlayerRef(), "Clans", "%s has promoted you to <green>%s</green>.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), memberRole.getName()));

                    this.getModule().getManager().messageClan(clan, "Clans", "%s has promoted %s to <green>%s</green>.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getPlayerName(ClanRelation.SELF, targetClient.getName()), memberRole.getName()), List.of(playerRef.getUuid(), targetClient.getId()));
                }
            });
        });
    }
}