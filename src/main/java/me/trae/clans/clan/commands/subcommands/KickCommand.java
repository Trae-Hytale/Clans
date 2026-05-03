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
import me.trae.clans.clan.events.MemberKickEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;

@Component
public class KickCommand extends AbstractClanSubCommand implements Listener {

    public KickCommand() {
        super("kick", "Kick a Member from the Clan");
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
            UtilMessage.message(playerRef, "Clans", "You did not input a Member to Kick.");
            return;
        }

        this.getModule().getManager().searchMember(playerClan, playerRef, args[0], true).ifPresent(targetClient -> {
            if (!(this.canKickPlayer(playerRef, client, playerClan, targetClient))) {
                return;
            }

            UtilEvent.dispatch(new MemberKickEvent(playerClan, playerRef, targetClient));
        });
    }

    private boolean canKickPlayer(final PlayerRef playerRef, final Client client, final Clan playerClan, final Client targetClient) {
        if (targetClient.equals(client)) {
            UtilMessage.message(playerRef, "Clans", "You cannot kick yourself!");
            return false;
        }

        if (!(client.isAdministrating())) {
            if (targetClient.isAdministrating() || playerClan.getMemberById(targetClient.getId()).map(member -> member.hasRole(playerClan.getMemberByPlayer(playerRef).map(Member::getRole).orElse(null))).orElse(false)) {
                UtilMessage.message(playerRef, "Clans", "You do not outrank %s!".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, targetClient.getName())));
                return false;
            }

            if (this.getModule().getManager().isBeingPillaged(playerClan)) {
                UtilMessage.message(playerRef, "Clans", "You cannot kick a member while being conquered by another clan!");
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMemberKick(final MemberKickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final Client targetClient = event.getTargetClient();

        clan.getMemberById(targetClient.getId()).ifPresent(member -> {
            clan.removeMember(member);
            this.getModule().getManager().getRepository().update(clan, ClanProperty.MEMBERS);

            this.getModule().getManager().getClanPlayerStorage().remove(member.getId());

            this.getModule().getManager().removeChatChannel(targetClient.getPlayerRef());

            UtilMessage.message(playerRef, "Clans", "You kicked %s from the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, targetClient.getName())));
            UtilMessage.message(targetClient.getPlayerRef(), "Clans", "%s kicked you from the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, playerRef)));

            this.getModule().getManager().messageClan(clan, "Clans", "%s kicked %s from the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, targetClient.getName())), Collections.singletonList(playerRef.getUuid()));
        });
    }
}