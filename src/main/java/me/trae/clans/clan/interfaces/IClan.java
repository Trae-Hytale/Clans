package me.trae.clans.clan.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.Alliance;
import me.trae.clans.clan.data.Enemy;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.data.Pillage;

import java.util.Optional;
import java.util.UUID;

public interface IClan {

    String getDisplayName();

    void addTerritory(final Chunk chunk);

    void removeTerritory(final Chunk chunk);

    boolean isTerritoryByChunk(final Chunk chunk);

    boolean hasTerritory();

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
}