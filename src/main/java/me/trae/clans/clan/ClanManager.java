package me.trae.clans.clan;

import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.AllArgsConstructor;
import me.trae.clans.Clans;
import me.trae.clans.clan.data.Alliance;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.interfaces.IClanManager;
import me.trae.clans.clan.wrappers.ClanUpdater;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Manager;
import me.trae.framework.updater.annotations.Update;
import me.trae.framework.updater.interfaces.Updater;
import me.trae.framework.utility.UtilColor;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.java.ConcurrentLinkedHashMap;
import me.trae.framework.utility.objects.Chunk;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@AllArgsConstructor
@Component
public class ClanManager implements Manager<Clans>, IClanManager, Updater {

    private final ConcurrentLinkedHashMap<UUID, Clan> CLAN_BY_ID_MAP = new ConcurrentLinkedHashMap<>();
    private final ConcurrentMap<String, Clan> CLAN_BY_NAME_MAP = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Clan> CLAN_BY_PLAYER_ID_MAP = new ConcurrentHashMap<>();
    private final ConcurrentMap<Chunk, Clan> CLAN_BY_CHUNK_MAP = new ConcurrentHashMap<>();

    private final List<ClanUpdater> clanUpdaterList;

    @Update(delay = 500L)
    public void onUpdater() {
        System.out.println("Test");
        for (final Clan clan : this.getClans()) {
            this.clanUpdaterList.forEach(clanUpdater -> clanUpdater.onUpdater(clan));
        }
    }

    @Override
    public void updateNameInClanCache(final String previousName, final Clan clan) {
        this.CLAN_BY_NAME_MAP.remove(previousName);
        this.CLAN_BY_NAME_MAP.put(clan.getName(), clan);
    }

    @Override
    public void updatePlayerInClanCache(final PlayerRef playerRef, final Clan clan) {
        if (clan == null) {
            this.CLAN_BY_PLAYER_ID_MAP.remove(playerRef.getUuid());
            return;
        }

        this.CLAN_BY_PLAYER_ID_MAP.put(playerRef.getUuid(), clan);
    }

    @Override
    public void updateChunkInClanCache(final Chunk chunk, final Clan clan) {
        if (clan == null) {
            this.CLAN_BY_CHUNK_MAP.remove(chunk);
            return;
        }

        this.CLAN_BY_CHUNK_MAP.put(chunk, clan);
    }

    @Override
    public List<Clan> getClans() {
        return this.CLAN_BY_ID_MAP.values().stream().toList();
    }

    @Override
    public void addClan(final Clan clan) {
        this.CLAN_BY_ID_MAP.put(clan.getId(), clan);
        this.CLAN_BY_NAME_MAP.put(clan.getName(), clan);

        if (clan.hasMembers()) {
            clan.getMembers().keySet().forEach(id -> this.CLAN_BY_PLAYER_ID_MAP.put(id, clan));
        }

        if (clan.hasTerritory()) {
            clan.getTerritory().forEach(chunk -> this.CLAN_BY_CHUNK_MAP.put(chunk, clan));
        }
    }

    @Override
    public void removeClan(final Clan clan) {
        this.CLAN_BY_ID_MAP.remove(clan.getId());
        this.CLAN_BY_NAME_MAP.remove(clan.getName());

        if (clan.hasMembers()) {
            clan.getMembers().keySet().forEach(this.CLAN_BY_PLAYER_ID_MAP::remove);
        }

        if (clan.hasTerritory()) {
            clan.getTerritory().forEach(this.CLAN_BY_CHUNK_MAP::remove);
        }
    }

    @Override
    public Optional<Clan> getClanById(final UUID id) {
        return Optional.ofNullable(this.CLAN_BY_ID_MAP.get(id));
    }

    @Override
    public Optional<Clan> getClanByName(final String name) {
        final Clan nameClan = this.CLAN_BY_NAME_MAP.get(name);
        if (nameClan != null && nameClan.getName().equals(name)) {
            return Optional.of(nameClan);
        }

        return this.getClans().stream().filter(clan -> clan.getName().equals(name)).findFirst().map(clan -> this.CLAN_BY_NAME_MAP.computeIfAbsent(name, __ -> clan));
    }

    @Override
    public Optional<Clan> getClanByPlayerId(final UUID id) {
        final Clan playerClan = this.CLAN_BY_PLAYER_ID_MAP.get(id);
        if (playerClan != null && playerClan.isMemberByPlayerId(id)) {
            return Optional.of(playerClan);
        }

        return this.getClans().stream().filter(clan -> clan.isMemberByPlayerId(id)).findFirst().map(clan -> this.CLAN_BY_PLAYER_ID_MAP.computeIfAbsent(id, __ -> clan));
    }

    @Override
    public Optional<Clan> getClanByPlayer(final PlayerRef playerRef) {
        return this.getClanByPlayerId(playerRef.getUuid());
    }

    @Override
    public Optional<Clan> getClanByChunk(final Chunk chunk) {
        final Clan chunkClan = this.CLAN_BY_CHUNK_MAP.get(chunk);
        if (chunkClan != null && chunkClan.isTerritory(chunk)) {
            return Optional.of(chunkClan);
        }

        return this.getClans().stream().filter(clan -> clan.isTerritory(chunk)).findFirst().map(clan -> this.CLAN_BY_CHUNK_MAP.computeIfAbsent(chunk, __ -> clan));
    }

    @Override
    public Optional<Clan> getClanByLocation(final Location location) {
        return this.getClanByChunk(Chunk.of(location));
    }

    @Override
    public ClanRelation getClanRelationByClan(final Clan clan, final Clan target) {
        if (clan != null && target != null) {
            if (clan.equals(target)) {
                return ClanRelation.SELF;
            }

            if (clan.isAllianceByClan(target)) {
                return clan.isTrustedAllianceByClan(target) ? ClanRelation.TRUSTED_ALLIANCE : ClanRelation.ALLIANCE;
            }

            if (clan.isEnemyByClan(target)) {
                return ClanRelation.ENEMY;
            }

            if (clan.isPillageByClan(target) || target.isPillageByClan(clan)) {
                return ClanRelation.PILLAGE;
            }
        }

        return ClanRelation.NEUTRAL;
    }

    @Override
    public ClanRelation getClanRelationByPlayer(final PlayerRef player, final PlayerRef target) {
        return this.getClanRelationByClan(this.getClanByPlayer(player).orElse(null), this.getClanByPlayer(target).orElse(null));
    }

    @Override
    public String getClanFullName(final ClanRelation clanRelation, final Clan clan) {
        return UtilColor.serialize(clanRelation.getSuffix(), "%s %s".formatted(clan.getType(), clan.getName()));
    }

    @Override
    public String getClanShortName(final ClanRelation clanRelation, final Clan clan) {
        return UtilColor.serialize(clanRelation.getSuffix(), clan.getName());
    }

    @Override
    public String getClanName(final ClanRelation clanRelation, final Clan clan) {
        return clan.isAdmin() ? this.getClanShortName(clanRelation, clan) : this.getClanFullName(clanRelation, clan);
    }

    @Override
    public void messageClan(final Clan clan, final String prefix, final String message, final List<UUID> ignore) {
        UtilMessage.message(clan.getMembersAsPlayers(), prefix, message, ignore);
    }

    @Override
    public void messageAllies(final Clan clan, final String prefix, final String message, final List<UUID> ignore) {
        for (final Alliance alliance : clan.getAlliances().values()) {
            this.getClanById(alliance.getId()).ifPresent(allianceClan -> this.messageClan(allianceClan, prefix, message, ignore));
        }
    }
}