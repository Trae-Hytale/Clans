package me.trae.clans.clan.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.*;
import me.trae.clans.clan.data.enums.RelationRequestType;

import java.util.Optional;
import java.util.UUID;

public interface IClan {

    boolean isOnline();

    boolean isSpawn();

    boolean isShops();

    boolean isFields();

    boolean isOutskirts();

    String getType();

    String getDisplayName();

    void addTerritory(final Chunk chunk);

    void removeTerritory(final Chunk chunk);

    boolean isTerritoryByChunk(final Chunk chunk);

    boolean hasTerritory();

    void addRequest(final Request request);

    void removeRequest(final Request request);

    void addRelationRequest(final Clan clan, final RelationRequestType type);

    void removeRelationRequest(final Clan clan, final RelationRequestType type);

    Optional<Request> getRelationRequestByClan(final Clan clan, final RelationRequestType type);

    void addInvitationRequest(final PlayerRef playerRef);

    void removeInvitationRequest(final PlayerRef playerRef);

    Optional<Request> getInvitationRequestByPlayer(final PlayerRef playerRef);

    void addMember(final Member member);

    void removeMember(final Member member);

    Optional<Member> getMemberById(final UUID id);

    Optional<Member> getMemberByPlayer(final PlayerRef playerRef);

    boolean isMemberById(final UUID id);

    boolean isMemberByPlayer(final PlayerRef playerRef);

    void addAlliance(final Alliance alliance);

    void removeAlliance(final Alliance alliance);

    Optional<Alliance> getAllianceById(final UUID id);

    Optional<Alliance> getAllianceByClan(final Clan clan);

    boolean isAllianceById(final UUID id);

    boolean isAllianceByClan(final Clan clan);

    boolean isTrustedAllianceByClan(final Clan clan);

    void addEnemy(final Enemy enemy);

    void removeEnemy(final Enemy enemy);

    Optional<Enemy> getEnemyById(final UUID id);

    Optional<Enemy> getEnemyByClan(final Clan clan);

    boolean isEnemyById(final UUID id);

    boolean isEnemyByClan(final Clan clan);

    void addPillage(final Pillage pillage);

    void removePillage(final Pillage pillage);

    Optional<Pillage> getPillageById(final UUID id);

    Optional<Pillage> getPillageByClan(final Clan clan);

    boolean isPillageById(final UUID id);

    boolean isPillageByClan(final Clan clan);

    boolean isNeutralByClan(final Clan clan);

    boolean hasHome();

    String getFormattedHomeLocation();

    void addEnergy(final long energy);

    void takeEnergy(final long energy);

    long getEnergyDepletion();

    boolean canDepleteEnergy();

    String getFormattedEnergyRemaining();
}