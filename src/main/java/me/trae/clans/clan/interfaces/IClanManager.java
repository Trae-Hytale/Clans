package me.trae.clans.clan.interfaces;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import io.github.trae.hytale.framework.wrappers.Chunk;
import io.github.trae.hytale.framework.wrappers.Location;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.enums.InteractType;
import me.trae.core.client.Client;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

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

    Optional<Clan> searchClan(final IMessageReceiver messageReceiver, final String name, final boolean inform, final Predicate<Clan> predicate);

    Optional<Clan> searchClan(final IMessageReceiver messageReceiver, final String name, final boolean inform);

    Optional<Client> searchMemberClient(final Clan clan, final IMessageReceiver messageReceiver, final String name, final boolean inform, final Predicate<Member> predicate);

    Optional<Client> searchMemberClient(final Clan clan, final IMessageReceiver messageReceiver, final String name, final boolean inform);

    ClanRelation getClanRelationByClan(final Clan clan, final Clan target);

    ClanRelation getClanRelationByPlayer(final PlayerRef playerRef, final PlayerRef targetPlayerRef);

    void showClanInformation(final PlayerRef playerRef, final Client playerClient, final Clan playerClan, final Clan targetClan);

    void removeChatChannel(final PlayerRef playerRef);

    void disbandClan(final Clan clan);

    void messageClan(final Clan clan, final String prefix, final String message, final List<UUID> ignored);

    String getClanName(final ClanRelation clanRelation, final Clan clan);

    String getClanFullName(final ClanRelation clanRelation, final Clan clan);

    String getClanShortName(final ClanRelation clanRelation, final Clan clan);

    String getPlayerName(final ClanRelation clanRelation, final String playerName);

    String getPlayerName(final ClanRelation clanRelation, final PlayerRef playerRef);

    Message getTerritoryClanNameForChat(final Clan playerClan, final Clan territoryClan, final BlockLocation blockLocation);

    Message getTerritoryClanNameForTitle(final Clan playerClan, final Clan territoryClan, final BlockLocation blockLocation);

    Message getTerritoryClanNameForSidebar(final Clan playerClan, final Clan territoryClan, final BlockLocation blockLocation);

    Color getClanMapColor(final Clan playerClan, final Clan territoryClan);

    int getMaxClaimLimit(final Clan clan);

    boolean isTerritoryFull(final Clan clan);

    int getSquadCount(final Clan clan);

    int getMaxSquadLimit(final Clan clan);

    boolean isSquadFull(final Clan clan);

    boolean canInteract(final PlayerRef playerRef, final Clan playerClan, final Clan territoryClan, final InteractType interactType);

    boolean canHurt(final PlayerRef damager, final PlayerRef damagee);
}