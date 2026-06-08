package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.territory.TerritoryUnClaimAllEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;
import java.util.List;

@Component
public class UnClaimAllCommand extends AbstractClanSubCommand implements EventListener {

    public UnClaimAllCommand() {
        super("unclaimall", "Unclaim All Territory");
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
        if (!(this.canUnClaimAll(playerRef, playerClan))) {
            return;
        }

        UtilEvent.dispatch(new TerritoryUnClaimAllEvent(playerClan, playerRef, List.copyOf(playerClan.getTerritory())));
    }

    private boolean canUnClaimAll(final PlayerRef playerRef, final Clan playerClan) {
        if (!(playerClan.hasTerritory())) {
            UtilMessage.message(playerRef, "Clans", "Your Clan does not have any territory to unclaim!");
            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTerritoryUnClaimAll(final TerritoryUnClaimAllEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final List<Chunk> chunks = event.getChunks();

        this.getModule().getManager().getBlockRestoreManager().unOutlineAllChunks(chunks, ClanManager.CHUNK_OUTLINE_BLOCK_RESTORE_NAME_FORMATTER.apply(clan));

        clan.getTerritory().removeAll(chunks);
        this.getModule().getManager().getRepository().update(clan, ClanProperty.TERRITORY);

        for (final Chunk chunk : chunks) {
            this.getModule().getManager().getClanChunkStorage().remove(chunk);
        }

        if (clan.getHome() != null) {
            clan.setHome(null);
            this.getModule().getManager().getRepository().update(clan, ClanProperty.HOME);
        }

        UtilMessage.message(playerRef, "Clans", "You unclaimed all territory.");

        this.getModule().getManager().messageClan(clan, "Clans", "%s has unclaimed all territory.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef)), Collections.singletonList(playerRef.getUuid()));
    }
}