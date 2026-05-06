package me.trae.clans.map.interfaces;

import com.hypixel.hytale.protocol.packets.worldmap.MapImage;
import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.clan.Clan;

import java.awt.*;
import java.util.List;

public interface IMapManager {

    MapImage getCachedOverlay(final int chunkX, final int chunkZ, final Color color);

    void cacheOverlay(final int chunkX, final int chunkZ, final Color color, final MapImage mapImage);

    void invalidateChunk(final Chunk chunk);

    void invalidateChunk(final int chunkX, final int chunkZ);

    void invalidateClanTerritory(final Clan clan);

    void refreshClanMembersMap(final Clan clan);

    void refreshClanMembersMapAgainst(final Clan clan, final Clan targetClan);

    void refreshPlayerClaimedChunks(final Player player, final Clan clan);

    void refreshChunksForWorld(final List<Chunk> chunkList);
}