package me.trae.clans.clan.interfaces;

import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.framework.utility.objects.Chunk;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClanManager {

    void updateNameInClanCache(final String previousName, final Clan clan);

    void updatePlayerInClanCache(final UUID id, Clan clan);

    void updateChunkInClanCache(final Chunk chunk, final Clan clan);

    List<Clan> getClans();

    void addClan(final Clan clan);

    void removeClan(final Clan clan);

    Optional<Clan> getClanById(final UUID id);

    Optional<Clan> getClanByName(final String name);

    Optional<Clan> getClanByPlayerId(final UUID id);

    Optional<Clan> getClanByPlayer(final PlayerRef player);

    Optional<Clan> getClanByChunk(final Chunk chunk);

    Optional<Clan> getClanByLocation(final Location location);

    ClanRelation getClanRelationByClan(final Clan clan, final Clan target);

    ClanRelation getClanRelationByPlayer(final PlayerRef player, final PlayerRef target);

    String getClanFullName(final ClanRelation clanRelation, final Clan clan);

    String getClanShortName(final ClanRelation clanRelation, final Clan clan);

    String getClanName(final ClanRelation clanRelation, final Clan clan);

    void messageClan(final Clan clan, final String prefix, final String message, final List<UUID> ignore);

    void messageAllies(final Clan clan, final String prefix, final String message, final List<UUID> ignore);
}