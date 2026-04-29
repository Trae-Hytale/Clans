package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilChunk;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanDisbandEvent;
import me.trae.core.client.Client;

@Component
public class DisbandCommand extends AbstractClanSubCommand implements Listener {

    public DisbandCommand() {
        super("disband", "Disband the Clan");
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
        if (!(this.canDisbandClan(playerRef, playerClan))) {
            return;
        }

        UtilEvent.dispatch(new ClanDisbandEvent(playerClan, playerRef));
    }

    private boolean canDisbandClan(final PlayerRef playerRef, final Clan clan) {
        if (this.getModule().getManager().getClanByChunk(UtilChunk.getChunkByPlayerRef(playerRef)).map(clan::isEnemyByClan).orElse(false)) {
            UtilMessage.message(playerRef, "Clans", "You cannot disband the clan while in enemy territory!");
            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanDisband(final ClanDisbandEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();

        for (final PlayerRef targetPlayerRef : Universe.get().getPlayers()) {
            final ClanRelation clanRelation = this.getModule().getManager().getClanRelationByClan(this.getModule().getManager().getClanByPlayer(targetPlayerRef).orElse(null), clan);

            UtilMessage.message(targetPlayerRef, "Clans", "%s has disbanded %s.".formatted(this.getModule().getManager().getPlayerName(clanRelation, playerRef), this.getModule().getManager().getClanFullName(clanRelation, clan)));
        }

        this.getModule().getManager().disbandClan(clan);
    }
}