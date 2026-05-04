package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.commands.subcommands.configs.LeaveCommandConfig;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.MemberLeaveEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Optional;

@Component
public class LeaveCommand extends AbstractClanSubCommand implements Listener {

    private final LeaveCommandConfig leaveCommandConfig;
    private final DisbandCommand disbandCommand;

    public LeaveCommand(final LeaveCommandConfig leaveCommandConfig, final DisbandCommand disbandCommand) {
        super("leave", "Leave the Clan");

        this.leaveCommandConfig = leaveCommandConfig;
        this.disbandCommand = disbandCommand;
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_PRESENT;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (!(this.canLeaveClan(playerRef, player, client, playerClan))) {
            return;
        }

        if (playerClan.getMembers().size() <= 1 & !(client.isAdministrating()) && !(playerClan.isAdmin())) {
            this.disbandCommand.execute(playerRef, player, client, playerClan, args);
            return;
        }

        UtilEvent.dispatch(new MemberLeaveEvent(playerClan, playerRef, client));
    }

    private boolean canLeaveClan(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan) {
        if (!(client.isAdministrating())) {
            if (playerClan.isAdmin()) {
                UtilMessage.message(playerRef, "Clans", "You cannot leave an Admin Clan without administrating!");
                return false;
            }

            if (playerClan.getMembers().size() > 1 && playerClan.getMemberByPlayer(playerRef).map(member -> member.hasRole(MemberRole.LEADER)).orElse(false)) {
                UtilMessage.message(playerRef, "Clans", "You must pass Leadership before leaving the Clan!");
                return false;
            }

            if (player.getWorld() != null) {
                final Optional<Clan> territoryClanOptional = this.getModule().getManager().getClanByChunk(Chunk.of(player.getWorld(), playerRef.getTransform().getPosition()));
                if (territoryClanOptional.isPresent() && playerClan.isEnemyByClan(territoryClanOptional.get())) {
                    UtilMessage.message(playerRef, "Clans", "You cannot leave the clan while in enemy territory!");
                    return false;
                }
            }

            if (this.leaveCommandConfig.isPillageCheck() && this.getModule().getManager().isBeingPillaged(playerClan)) {
                UtilMessage.message(playerRef, "Clans", "You cannot leave the clan while being conquered by another clan!");
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMemberLeave(final MemberLeaveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();

        clan.getMemberByPlayer(playerRef).ifPresent(member -> {
            clan.removeMember(member);
            this.getModule().getManager().getRepository().update(clan, ClanProperty.MEMBERS);

            this.getModule().getManager().getClanPlayerStorage().remove(member.getId());

            if (!(clan.isOnline()) && !(event.getPlayerClient().isAdministrating())) {
                clan.setLastOnline(System.currentTimeMillis());
                this.getModule().getManager().getRepository().update(clan, ClanProperty.LAST_ONLINE);
            }

            this.getModule().getManager().removeChatChannel(playerRef);

            UtilMessage.message(playerRef, "Clans", "You left %s.".formatted(this.getModule().getManager().getClanShortName(ClanRelation.NEUTRAL, clan)));

            this.getModule().getManager().messageClan(clan, "Clans", "%s has left the Clan.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.NEUTRAL, playerRef)), null);
        });
    }
}