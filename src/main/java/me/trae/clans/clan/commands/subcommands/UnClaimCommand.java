package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilChunk;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.territory.TerritoryUnClaimEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;
import java.util.Optional;

@Component
public class UnClaimCommand extends AbstractClanSubCommand implements EventListener {

    public UnClaimCommand() {
        super("unclaim", "Unclaim Territory");
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
        final World world = player.getWorld();
        if (world == null) {
            return;
        }

        final Chunk chunk = Chunk.of(world, playerRef.getTransform().getPosition());

        if (!(this.canUnClaimChunk(playerRef, client, playerClan, chunk))) {
            return;
        }

        UtilEvent.dispatch(new TerritoryUnClaimEvent(playerClan, playerRef, chunk));
    }

    private boolean canUnClaimChunk(final PlayerRef playerRef, final Client client, final Clan playerClan, final Chunk chunk) {
        final Optional<Clan> territoryClanOptional = this.getModule().getManager().getClanByChunk(chunk);
        if (territoryClanOptional.isEmpty()) {
            UtilMessage.message(playerRef, "Clans", "This Territory is not owned by anyone!");
            return false;
        }

        final Clan territoryClan = territoryClanOptional.get();

        if (!(client.isAdministrating())) {
            if (!(chunk.getWorldName().equals(this.getModule().getManager().getTerritoryConfig().getAllowedWorldName()))) {
                UtilMessage.message(playerRef, "Clans", "You cannot unclaim land in this world!");
                return false;
            }

            if (!(territoryClan.equals(playerClan))) {
                UtilMessage.message(playerRef, "Clans", "This Territory is not owned by your Clan!");
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTerritoryUnClaim(final TerritoryUnClaimEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final Chunk chunk = event.getChunk();

        clan.removeTerritory(chunk);
        this.getModule().getManager().getRepository().update(clan, ClanProperty.TERRITORY);

        this.getModule().getManager().getClanChunkStorage().remove(chunk);

        Optional.ofNullable(clan.getHome()).ifPresent(home -> {
            if (home.getChunk().equals(chunk)) {
                clan.setHome(null);
                this.getModule().getManager().getRepository().update(clan, ClanProperty.HOME);

                event.setHomeDeleted(true);
            }
        });

        this.getModule().getManager().getBlockRestoreManager().unOutlineChunk(chunk, ClanManager.CHUNK_OUTLINE_BLOCK_RESTORE_NAME_FORMATTER.apply(clan));

        final String chunkString = "(%s)".formatted(UtilChunk.formatChunk(chunk, ChatColor.YELLOW.getColor()));

        UtilMessage.message(playerRef, "Clans", "You unclaimed territory at %s.".formatted(chunkString));

        this.getModule().getManager().messageClan(clan, "Clans", "%s has unclaimed territory at %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), chunkString), Collections.singletonList(playerRef.getUuid()));

        if (event.isHomeDeleted()) {
            this.getModule().getManager().messageClan(clan, "Clans", "The Clan Home was also deleted with the territory being unclaimed!", null);
        }
    }
}