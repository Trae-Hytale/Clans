package me.trae.clans.map.packets;

import com.hypixel.hytale.protocol.Packet;
import com.hypixel.hytale.protocol.packets.worldmap.*;
import com.hypixel.hytale.server.core.io.adapter.PlayerPacketFilter;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.palette.BitFieldArr;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.packet.OutboundPacketFilter;
import io.github.trae.hytale.framework.wrappers.Chunk;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.map.MapManager;
import me.trae.core.client.Client;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Getter
@Component
public class MapOverlayPacketFilter implements Module<ClansPlugin, MapManager>, PlayerPacketFilter, OutboundPacketFilter {

    private static final int BORDER_SIZE = 2;
    private static final float BORDER_OVERLAY_ALPHA = 0.75f;
    private static final float INTERIOR_OVERLAY_ALPHA = 0.4f;

    @Override
    public boolean test(@Nonnull final PlayerRef playerRef, @Nonnull final Packet packet) {
        if (!(packet instanceof final UpdateWorldMap updateWorldMap)) {
            return false;
        }

        final ClanManager clanManager = this.getManager().getClanManager();
        final Clan playerClan = clanManager.getClanByPlayer(playerRef).orElse(null);

        if (updateWorldMap.chunks != null && updateWorldMap.chunks.length > 0) {
            final String worldName = this.getWorldName(playerRef);

            if (worldName != null) {
                for (int chunkIndex = 0; chunkIndex < updateWorldMap.chunks.length; chunkIndex++) {
                    final MapChunk mapChunk = updateWorldMap.chunks[chunkIndex];
                    if (mapChunk == null || mapChunk.image == null) {
                        continue;
                    }

                    final Clan territoryClan = clanManager.getClanByChunk(new Chunk(worldName, mapChunk.chunkX, mapChunk.chunkZ)).orElse(null);
                    if (territoryClan == null) {
                        continue;
                    }

                    final Color color = clanManager.getClanMapColor(playerClan, territoryClan);

                    final MapImage cachedMapImage = this.getManager().getCachedOverlay(mapChunk.chunkX, mapChunk.chunkZ, color);
                    if (cachedMapImage != null) {
                        updateWorldMap.chunks[chunkIndex] = new MapChunk(mapChunk.chunkX, mapChunk.chunkZ, cachedMapImage);
                        continue;
                    }

                    final Clan[] neighborClans = {
                            clanManager.getClanByChunk(new Chunk(worldName, mapChunk.chunkX, mapChunk.chunkZ + 1)).orElse(null),
                            clanManager.getClanByChunk(new Chunk(worldName, mapChunk.chunkX, mapChunk.chunkZ - 1)).orElse(null),
                            clanManager.getClanByChunk(new Chunk(worldName, mapChunk.chunkX + 1, mapChunk.chunkZ)).orElse(null),
                            clanManager.getClanByChunk(new Chunk(worldName, mapChunk.chunkX - 1, mapChunk.chunkZ)).orElse(null),
                    };

                    final MapImage overlaidMapImage = this.applyOverlay(mapChunk.image, color, territoryClan, neighborClans);

                    this.getManager().cacheOverlay(mapChunk.chunkX, mapChunk.chunkZ, color, overlaidMapImage);

                    updateWorldMap.chunks[chunkIndex] = new MapChunk(mapChunk.chunkX, mapChunk.chunkZ, overlaidMapImage);
                }
            }
        }

        this.processMarkers(updateWorldMap, playerRef, playerClan);

        return false;
    }

    private String getWorldName(final PlayerRef playerRef) {
        try {
            if (playerRef.getWorldUuid() != null) {
                final World world = Universe.get().getWorld(playerRef.getWorldUuid());
                if (world != null) {
                    return world.getName();
                }
            }
        } catch (final Exception ignored) {
        }

        return null;
    }

    private MapImage applyOverlay(final MapImage originalMapImage, final Color color, final Clan territoryClan, final Clan[] neighborClans) {
        final int colorRgb = color.getRGB() & 0xFFFFFF;

        final int width = originalMapImage.width;
        final int height = originalMapImage.height;

        final int[] pixels = this.unpackPixels(originalMapImage.palette, originalMapImage.bitsPerIndex, originalMapImage.packedIndices, width * height);

        final int overlayRed = (colorRgb >> 16) & 0xFF;
        final int overlayGreen = (colorRgb >> 8) & 0xFF;
        final int overlayBlue = colorRgb & 0xFF;

        for (int pixelX = 0; pixelX < width; pixelX++) {
            for (int pixelZ = 0; pixelZ < height; pixelZ++) {
                final int pixelIndex = pixelZ * width + pixelX;
                final int argb = pixels[pixelIndex];

                final int alpha = argb & 0xFF;
                if (alpha == 0) {
                    continue;
                }

                final int red = (argb >> 24) & 0xFF;
                final int green = (argb >> 16) & 0xFF;
                final int blue = (argb >> 8) & 0xFF;

                final boolean isBorder = (pixelX <= BORDER_SIZE && !(territoryClan.equals(neighborClans[3])))
                        || (pixelX >= width - BORDER_SIZE - 1 && !(territoryClan.equals(neighborClans[2])))
                        || (pixelZ <= BORDER_SIZE && !(territoryClan.equals(neighborClans[1])))
                        || (pixelZ >= height - BORDER_SIZE - 1 && !(territoryClan.equals(neighborClans[0])));

                final float overlayAlpha = isBorder ? BORDER_OVERLAY_ALPHA : INTERIOR_OVERLAY_ALPHA;
                final float inverseAlpha = 1.0f - overlayAlpha;

                final int newRed = (int) (red * inverseAlpha + overlayRed * overlayAlpha);
                final int newGreen = (int) (green * inverseAlpha + overlayGreen * overlayAlpha);
                final int newBlue = (int) (blue * inverseAlpha + overlayBlue * overlayAlpha);

                pixels[pixelIndex] = ((newRed & 0xFF) << 24) | ((newGreen & 0xFF) << 16) | ((newBlue & 0xFF) << 8) | (alpha & 0xFF);
            }
        }

        return this.repackImage(pixels, width, height);
    }

    private int[] unpackPixels(final int[] palette, final byte bitsPerIndex, final byte[] packedIndices, final int pixelCount) {
        final int[] pixels = new int[pixelCount];
        final long mask = (1L << bitsPerIndex) - 1;

        for (int pixelIndex = 0; pixelIndex < pixelCount; pixelIndex++) {
            final long bitOffset = (long) pixelIndex * bitsPerIndex;
            final int byteOffset = (int) (bitOffset >> 3);
            final int bitShift = (int) (bitOffset & 7);

            long rawBits = 0;
            final int bytesNeeded = (bitShift + bitsPerIndex + 7) >> 3;
            for (int byteIndex = 0; byteIndex < bytesNeeded && (byteOffset + byteIndex) < packedIndices.length; byteIndex++) {
                rawBits |= ((long) (packedIndices[byteOffset + byteIndex] & 0xFF)) << (byteIndex * 8);
            }

            final int paletteIndex = (int) ((rawBits >> bitShift) & mask);
            if (paletteIndex < palette.length) {
                pixels[pixelIndex] = palette[paletteIndex];
            }
        }

        return pixels;
    }

    private MapImage repackImage(final int[] pixels, final int width, final int height) {
        final IntOpenHashSet uniqueColorsSet = new IntOpenHashSet();
        for (final int pixel : pixels) {
            uniqueColorsSet.add(pixel);
        }

        final int[] newPalette = uniqueColorsSet.toIntArray();
        final int bitsPerIndex = this.calculateBitsRequired(newPalette.length);

        final Int2IntOpenHashMap colorToIndex = new Int2IntOpenHashMap(newPalette.length);
        for (int paletteIndex = 0; paletteIndex < newPalette.length; paletteIndex++) {
            colorToIndex.put(newPalette[paletteIndex], paletteIndex);
        }

        final BitFieldArr indices = new BitFieldArr(bitsPerIndex, pixels.length);
        for (int pixelIndex = 0; pixelIndex < pixels.length; pixelIndex++) {
            indices.set(pixelIndex, colorToIndex.get(pixels[pixelIndex]));
        }

        return new MapImage(width, height, newPalette, (byte) bitsPerIndex, indices.get());
    }

    private int calculateBitsRequired(final int colorCount) {
        if (colorCount <= 16) {
            return 4;
        }

        if (colorCount <= 256) {
            return 8;
        }

        return colorCount <= 4096 ? 12 : 16;
    }

    private void processMarkers(final UpdateWorldMap updateWorldMap, final PlayerRef playerRef, final Clan playerClan) {
        if (updateWorldMap.addedMarkers == null || updateWorldMap.addedMarkers.length == 0) {
            return;
        }

        final ClanManager clanManager = this.getManager().getClanManager();

        final boolean administrating = clanManager.getClientManager().getClientByPlayer(playerRef).map(Client::isAdministrating).orElse(false);

        List<MapMarker> filteredMapMarkerList = null;

        for (int i = 0; i < updateWorldMap.addedMarkers.length; i++) {
            final MapMarker mapMarker = updateWorldMap.addedMarkers[i];
            final UUID markerPlayerId = this.getPlayerIdFromMarker(mapMarker);

            if (markerPlayerId == null) {
                if (filteredMapMarkerList != null) {
                    filteredMapMarkerList.add(mapMarker);
                }
                continue;
            }

            if (markerPlayerId.equals(playerRef.getUuid())) {
                if (filteredMapMarkerList != null) {
                    filteredMapMarkerList.add(mapMarker);
                }
                continue;
            }

            if (administrating) {
                final Clan markerClan = clanManager.getClanByPlayerId(markerPlayerId).orElse(null);
                final ClanRelation clanRelation = clanManager.getClanRelationByClan(playerClan, markerClan);

                this.applyMarkerTint(mapMarker, clanRelation);
                continue;
            }

            if (playerClan == null) {
                if (filteredMapMarkerList == null) {
                    filteredMapMarkerList = new ArrayList<>(updateWorldMap.addedMarkers.length);
                    filteredMapMarkerList.addAll(Arrays.asList(updateWorldMap.addedMarkers).subList(0, i));
                }
                continue;
            }

            final Clan markerClan = clanManager.getClanByPlayerId(markerPlayerId).orElse(null);
            final ClanRelation clanRelation = clanManager.getClanRelationByClan(playerClan, markerClan);

            if (ClanRelation.isTeammate(clanRelation)) {
                this.applyMarkerTint(mapMarker, clanRelation);
                if (filteredMapMarkerList != null) {
                    filteredMapMarkerList.add(mapMarker);
                }
            } else {
                if (filteredMapMarkerList == null) {
                    filteredMapMarkerList = new ArrayList<>(updateWorldMap.addedMarkers.length);
                    filteredMapMarkerList.addAll(Arrays.asList(updateWorldMap.addedMarkers).subList(0, i));
                }
            }
        }

        if (filteredMapMarkerList != null) {
            updateWorldMap.addedMarkers = filteredMapMarkerList.toArray(new MapMarker[0]);
        }
    }

    private UUID getPlayerIdFromMarker(final MapMarker mapMarker) {
        if (mapMarker.components == null) {
            return null;
        }

        for (final MapMarkerComponent mapMarkerComponent : mapMarker.components) {
            if (mapMarkerComponent instanceof final PlayerMarkerComponent playerMarkerComponent) {
                return playerMarkerComponent.playerId;
            }
        }

        return null;
    }

    private void applyMarkerTint(final MapMarker mapMarker, final ClanRelation clanRelation) {
        final Color awtColor = clanRelation.getSuffix();
        final com.hypixel.hytale.protocol.Color tintColor = new com.hypixel.hytale.protocol.Color(
                (byte) awtColor.getRed(),
                (byte) awtColor.getGreen(),
                (byte) awtColor.getBlue()
        );

        final TintComponent tintComponent = new TintComponent(tintColor);

        if (mapMarker.components == null) {
            mapMarker.components = new MapMarkerComponent[]{tintComponent};
            return;
        }

        // Replace existing TintComponent or append
        for (int i = 0; i < mapMarker.components.length; i++) {
            if (mapMarker.components[i] instanceof TintComponent) {
                mapMarker.components[i] = tintComponent;
                return;
            }
        }

        mapMarker.components = Arrays.copyOf(mapMarker.components, mapMarker.components.length + 1);
        mapMarker.components[mapMarker.components.length - 1] = tintComponent;
    }
}