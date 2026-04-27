package me.trae.clans.clan.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.wrappers.Chunk;
import io.github.trae.hytale.framework.wrappers.Location;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.enums.ClanRelation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClanManager {

    List<Clan> getClans();

    void flushAllClans();

    void addClan(final Clan clan);

    void removeClan(final Clan clan);

    Optional<Clan> getClanById(final UUID id);

    Optional<Clan> getClanByName(final String name);

    Optional<Clan> getClanByPlayerId(final UUID playerId);

    Optional<Clan> getClanByPlayer(final PlayerRef playerRef);

    Optional<Clan> getClanByChunk(final Chunk chunk);

    Optional<Clan> getClanByLocation(final Location location);

    ClanRelation getClanRelationByClan(final Clan clan, final Clan target);

    ClanRelation getClanRelationByPlayer(final PlayerRef playerRef, final PlayerRef targetPlayerRef);

    String getClanName(final ClanRelation clanRelation, final Clan clan);

    String getClanFullName(final ClanRelation clanRelation, final Clan clan);

    String getClanShortName(final ClanRelation clanRelation, final Clan clan);

    void showClanInformation(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan);

    void disbandClan(final Clan clan);

    int getMaxClaimLimit(final Clan clan);
}