package me.trae.clans.map;

import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.protocol.packets.worldmap.MapImage;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.hytale.framework.wrappers.Chunk;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.data.Member;
import me.trae.clans.map.interfaces.IMapManager;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Service
public class MapManager implements Manager<ClansPlugin>, IMapManager {

    private final ConcurrentHashMap<Long, ConcurrentHashMap<Integer, MapImage>> overlayCache = new ConcurrentHashMap<>();

    @Getter
    private final ClanManager clanManager;

    @Override
    public MapImage getCachedOverlay(final int chunkX, final int chunkZ, final Color color) {
        final ConcurrentHashMap<Integer, MapImage> cached = this.overlayCache.get(ChunkUtil.indexChunk(chunkX, chunkZ));
        if (cached == null) {
            return null;
        }

        return cached.get(color.getRGB() & 0xFFFFFF);
    }

    @Override
    public void cacheOverlay(final int chunkX, final int chunkZ, final Color color, final MapImage mapImage) {
        this.overlayCache.computeIfAbsent(ChunkUtil.indexChunk(chunkX, chunkZ), _ -> new ConcurrentHashMap<>()).put(color.getRGB() & 0xFFFFFF, mapImage);
    }

    @Override
    public void invalidateChunk(final int chunkX, final int chunkZ) {
        this.overlayCache.remove(ChunkUtil.indexChunk(chunkX, chunkZ));
        this.overlayCache.remove(ChunkUtil.indexChunk(chunkX, chunkZ + 1));
        this.overlayCache.remove(ChunkUtil.indexChunk(chunkX, chunkZ - 1));
        this.overlayCache.remove(ChunkUtil.indexChunk(chunkX + 1, chunkZ));
        this.overlayCache.remove(ChunkUtil.indexChunk(chunkX - 1, chunkZ));
    }

    @Override
    public void invalidateChunk(final Chunk chunk) {
        this.invalidateChunk(chunk.getX(), chunk.getZ());
    }

    @Override
    public void refreshPlayer(final Player player, final Clan clan) {
        if (player == null || clan == null) {
            return;
        }

        final World world = player.getWorld();
        if (world == null) {
            return;
        }

        world.execute(() -> {
            final LongOpenHashSet chunkIndices = new LongOpenHashSet();

            this.collectTerritoryIndices(clan, chunkIndices);

            for (final UUID allianceId : clan.getAlliances().keySet()) {
                this.clanManager.getClanById(allianceId).ifPresent(allyClan -> this.collectTerritoryIndices(allyClan, chunkIndices));
            }

            for (final UUID enemyId : clan.getEnemies().keySet()) {
                this.clanManager.getClanById(enemyId).ifPresent(enemyClan -> this.collectTerritoryIndices(enemyClan, chunkIndices));
            }

            for (final UUID pillageId : clan.getPillages().keySet()) {
                this.clanManager.getClanById(pillageId).ifPresent(pillageClan -> this.collectTerritoryIndices(pillageClan, chunkIndices));
            }

            for (final UUID pillagerId : clan.getPillagers()) {
                this.clanManager.getClanById(pillagerId).ifPresent(pillagerClan -> this.collectTerritoryIndices(pillagerClan, chunkIndices));
            }

            if (!(chunkIndices.isEmpty())) {
                player.getWorldMapTracker().clearChunks(chunkIndices);
            }
        });
    }

    @Override
    public void refreshClan(final Clan clan, final Clan targetClan) {
        for (final Member member : clan.getMembers().values()) {
            final Player player = member.getPlayer();
            if (player == null) {
                continue;
            }

            final World world = player.getWorld();
            if (world == null) {
                continue;
            }

            world.execute(() -> {
                final LongOpenHashSet chunkIndices = new LongOpenHashSet();
                this.collectTerritoryIndices(targetClan, chunkIndices);

                if (!(chunkIndices.isEmpty())) {
                    player.getWorldMapTracker().clearChunks(chunkIndices);
                }
            });
        }
    }

    @Override
    public void refreshChunks(final List<Chunk> chunkList) {
        if (chunkList.isEmpty()) {
            return;
        }

        final Map<World, LongOpenHashSet> chunkIndicesByWorld = new HashMap<>();

        for (final Chunk chunk : chunkList) {
            this.invalidateChunk(chunk);
            chunkIndicesByWorld.computeIfAbsent(chunk.getWorld(), _ -> new LongOpenHashSet()).add(ChunkUtil.indexChunk(chunk.getX(), chunk.getZ()));
        }

        for (final Map.Entry<World, LongOpenHashSet> entry : chunkIndicesByWorld.entrySet()) {
            final LongOpenHashSet chunkIndices = entry.getValue();

            for (final PlayerRef playerRef : entry.getKey().getPlayerRefs()) {
                UtilPlayer.getPlayer(playerRef).ifPresent(player -> player.getWorldMapTracker().clearChunks(chunkIndices));
            }
        }
    }

    private void collectTerritoryIndices(final Clan clan, final LongOpenHashSet chunkIndicesSet) {
        for (final Chunk chunk : clan.getTerritory()) {
            chunkIndicesSet.add(ChunkUtil.indexChunk(chunk.getX(), chunk.getZ()));
        }
    }
}