package me.trae.clans.clan.commands.subcommands.admin;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.utility.UtilColor;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.hytale.framework.wrappers.Chunk;
import io.github.trae.utilities.UtilInput;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.commands.subcommands.admin.configs.MassClaimCommandConfig;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.TerritoryMassClaimEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.blockrestore.BlockRestore;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class MassClaimCommand extends AbstractClanSubCommand {

    private final MassClaimCommandConfig massClaimCommandConfig;

    public MassClaimCommand(final MassClaimCommandConfig massClaimCommandConfig) {
        super("massclaim", "Massclaim Clan Territory", Rank.OWNER);

        this.massClaimCommandConfig = massClaimCommandConfig;
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_PRESENT;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (args.length == 0) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Size.");
            return;
        }

        final Optional<Integer> sizeOptional = UtilInput.getNumber(Integer.class, 1, 20, args[0]);
        if (sizeOptional.isEmpty()) {
            UtilMessage.message(playerRef, "Clans", "You did not input a valid Size.");
            return;
        }

        final World world = player.getWorld();
        if (world == null) {
            return;
        }

        final int size = sizeOptional.get();

        final Chunk chunk = Chunk.of(world, playerRef.getTransform().getPosition());

        final Set<Clan> territoryClanSet = new HashSet<>();

        final List<Chunk> nearbyChunkList = chunk.getNearbyChunks(size, true);

        final List<Chunk> claimedChunkList = new ArrayList<>();

        for (final Chunk nearbyChunk : nearbyChunkList) {
            final Optional<Clan> territoryClanOptional = this.getModule().getManager().getClanByChunk(nearbyChunk);
            if (territoryClanOptional.isPresent()) {
                final Clan territoryClan = territoryClanOptional.get();

                if (territoryClan.equals(playerClan)) {
                    continue;
                }

                territoryClan.removeTerritory(nearbyChunk);

                territoryClanSet.add(territoryClan);
            }

            playerClan.addTerritory(nearbyChunk);

            this.getModule().getManager().getClanChunkStorage().put(nearbyChunk, playerClan);

            claimedChunkList.add(nearbyChunk);
        }

        if (claimedChunkList.isEmpty()) {
            UtilMessage.message(playerRef, "Clans", "You already own all chunks in that area.");
            return;
        }

        this.getModule().getManager().getRepository().update(playerClan, ClanProperty.TERRITORY);

        territoryClanSet.forEach(territoryClan -> this.getModule().getManager().getRepository().update(territoryClan, ClanProperty.TERRITORY));

        if (this.massClaimCommandConfig.isOutlineEnabled()) {
            final String blockRestoreName = ClanManager.CHUNK_OUTLINE_BLOCK_RESTORE_NAME_FORMATTER.apply(playerClan);
            final String outlineBlockId = this.massClaimCommandConfig.getOutlineBlockId();
            final long outlineDuration = this.massClaimCommandConfig.getOutlineDuration();

            if (this.massClaimCommandConfig.isDrawOutlineForEachChunk()) {
                this.getModule().getManager().getBlockRestoreManager().outlineAllChunks(claimedChunkList, blockRestoreName, outlineBlockId, outlineDuration);
            } else {
                final List<BlockRestore> blockRestoreList = Chunk.getTotalOutlineOfAllChunks(claimedChunkList).stream().map(blockLocation -> new BlockRestore(blockRestoreName, blockLocation, outlineBlockId, outlineDuration)).toList();

                this.getModule().getManager().getBlockRestoreManager().applyAll(blockRestoreList);
            }
        }

        UtilMessage.message(playerRef, "Clans", "You have claimed <yellow>%s</yellow>x chunks for %s.".formatted(claimedChunkList.size(), this.getModule().getManager().getClanFullName(ClanRelation.SELF, playerClan)));

        if (!(territoryClanSet.isEmpty())) {
            UtilMessage.message(playerRef, "Clans", "You also claimed over <yellow>%s</yellow> clans: [%s]".formatted(territoryClanSet.size(), territoryClanSet.stream().map(territoryClan -> this.getModule().getManager().getClanShortName(this.getModule().getManager().getClanRelationByClan(playerClan, territoryClan), territoryClan)).collect(Collectors.joining(UtilColor.serialize(ChatColor.GRAY.getColor(), ", ")))));
        }

        UtilEvent.dispatch(new TerritoryMassClaimEvent(playerClan, playerRef, claimedChunkList, territoryClanSet));
    }
}
