package me.trae.clans.clan.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.clan.AdminClan;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.Alliance;
import me.trae.clans.clan.data.Enemy;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.data.Pillage;
import me.trae.clans.clan.enums.RelationRequestType;
import me.trae.framework.utility.objects.Chunk;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IClan {

    default boolean isAdmin() {
        return this instanceof AdminClan;
    }

    default String getType() {
        return this.isAdmin() ? "Admin Clan" : "Clan";
    }

    String getDisplayName();

    void addRelationRequest(final RelationRequestType relationRequestType, final Clan clan);

    void removeRelationRequest(final RelationRequestType relationRequestType, final Clan clan);

    boolean isRelationRequest(final RelationRequestType relationRequestType, final Clan clan);

    void addInvitationRequest(final PlayerRef player);

    void removeInvitationRequest(final PlayerRef player);

    boolean isInvitationRequest(final PlayerRef player);

    void addMember(final Member member);

    void removeMember(final Member member);

    Optional<Member> getMemberByPlayerId(final UUID id);

    Optional<Member> getMemberByPlayer(final PlayerRef player);

    boolean isMemberByPlayerId(final UUID id);

    boolean isMemberByPlayer(final PlayerRef player);

    boolean hasMembers();

    List<PlayerRef> getMembersAsPlayers();

    void addAlliance(final Alliance alliance);

    void removeAlliance(final Alliance alliance);

    Optional<Alliance> getAllianceByClan(final Clan clan);

    boolean isAllianceByClan(final Clan clan);

    boolean isTrustedAllianceByClan(final Clan clan);

    boolean hasAlliances();

    void addEnemy(final Enemy enemy);

    void removeEnemy(final Enemy enemy);

    Optional<Enemy> getEnemyByClan(final Clan clan);

    boolean isEnemyByClan(final Clan clan);

    boolean hasEnemies();

    void addPillage(final Pillage pillage);

    void removePillage(final Pillage pillage);

    Optional<Pillage> getPillageByClan(final Clan clan);

    boolean isPillageByClan(final Clan clan);

    boolean hasPillages();

    void addTerritory(final Chunk chunk);

    void removeTerritory(final Chunk chunk);

    boolean hasTerritory();

    boolean isTerritory(final Chunk chunk);

    boolean hasHome();
}