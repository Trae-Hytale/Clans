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
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanUnClaimAllEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.client.Client;

import java.util.Collections;
import java.util.List;

@Component
public class UnClaimAllCommand extends AbstractClanSubCommand implements Listener {

    private final BlockRestoreManager blockRestoreManager;

    public UnClaimAllCommand(final BlockRestoreManager blockRestoreManager) {
        super("unclaimall", "Unclaim All Territory");

        this.blockRestoreManager = blockRestoreManager;
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

        UtilEvent.dispatch(new ClanUnClaimAllEvent(playerClan, playerRef, playerClan.getTerritory()));
    }

    private boolean canUnClaimAll(final PlayerRef playerRef, final Clan playerClan) {
        if (!(playerClan.hasTerritory())) {
            UtilMessage.message(playerRef, "Clans", "Your Clan does not have any territory to unclaim!");
            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanUnClaimAll(final ClanUnClaimAllEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final List<Chunk> chunks = event.getChunks();

        for (final Chunk chunk : chunks) {
            this.getModule().getManager().getClanChunkStorage().remove(chunk);

            this.blockRestoreManager.unOutlineChunk(chunk, "Clan:%s".formatted(clan.getName()));
        }

        clan.getTerritory().clear();
        this.getModule().getManager().getRepository().update(clan, ClanProperty.TERRITORY);

        if (clan.getHome() != null) {
            clan.setHome(null);
            this.getModule().getManager().getRepository().update(clan, ClanProperty.HOME);
        }

        UtilMessage.message(playerRef, "Clans", "You unclaimed all territory.");

        this.getModule().getManager().messageClan(clan, "Clans", "%s has unclaimed all territory.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef)), Collections.singletonList(playerRef.getUuid()));
    }
}