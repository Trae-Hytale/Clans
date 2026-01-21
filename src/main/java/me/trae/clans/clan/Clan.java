package me.trae.clans.clan;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.trae.clans.clan.data.*;
import me.trae.clans.clan.enums.RelationRequestType;
import me.trae.clans.clan.interfaces.IClan;
import me.trae.framework.utility.objects.Chunk;

import javax.xml.stream.Location;
import java.util.*;

@RequiredArgsConstructor
@Getter
@Setter
public class Clan implements IClan {

    private final UUID id;

    private String name;

    private final Map<UUID, Map<RelationRequestType, Request>> relationRequests = new HashMap<>();
    private final Map<UUID, Request> invitationRequests = new HashMap<>();

    private final LinkedHashMap<UUID, Member> members = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, Alliance> alliances = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, Enemy> enemies = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, Pillage> pillages = new LinkedHashMap<>();

    private final List<Chunk> territory = new ArrayList<>();

    private Location home;

    @Override
    public void addRelationRequest(final RelationRequestType relationRequestType, final Clan clan) {
        this.getRelationRequests().computeIfAbsent(clan.getId(), __ -> new HashMap<>()).put(relationRequestType, new Request(clan.getId(), System.currentTimeMillis()));
    }

    @Override
    public void removeRelationRequest(final RelationRequestType relationRequestType, final Clan clan) {
        this.getRelationRequests().computeIfPresent(clan.getId(), (__, map) -> map.remove(relationRequestType) != null && map.isEmpty() ? null : map);
    }

    @Override
    public boolean isRelationRequest(final RelationRequestType relationRequestType, final Clan clan) {
        return this.getRelationRequests().getOrDefault(clan.getId(), Collections.emptyMap()).containsKey(relationRequestType);
    }

    @Override
    public void addInvitationRequest(final PlayerRef player) {
        this.getInvitationRequests().put(player.getUuid(), new Request(player.getUuid(), System.currentTimeMillis()));
    }

    @Override
    public void removeInvitationRequest(final PlayerRef player) {
        this.getInvitationRequests().remove(player.getUuid());
    }

    @Override
    public boolean isInvitationRequest(final PlayerRef player) {
        return this.getInvitationRequests().containsKey(player.getUuid());
    }

    @Override
    public void addMember(final Member member) {
        this.getMembers().put(member.getId(), member);
    }

    @Override
    public void removeMember(final Member member) {
        this.getMembers().remove(member.getId());
    }

    @Override
    public Optional<Member> getMemberByPlayerId(final UUID id) {
        return Optional.ofNullable(this.getMembers().get(id));
    }

    @Override
    public Optional<Member> getMemberByPlayer(final PlayerRef player) {
        return this.getMemberByPlayerId(player.getUuid());
    }

    @Override
    public boolean isMemberByPlayerId(final UUID id) {
        return this.getMembers().containsKey(id);
    }

    @Override
    public boolean isMemberByPlayer(final PlayerRef player) {
        return this.isMemberByPlayerId(player.getUuid());
    }

    @Override
    public boolean hasMembers() {
        return !(this.getMembers().isEmpty());
    }

    @Override
    public List<PlayerRef> getMembersAsPlayers() {
        return this.getMembers().values().stream().filter(Member::isOnline).map(Member::getPlayer).toList();
    }

    @Override
    public void addAlliance(final Alliance alliance) {
        this.getAlliances().put(alliance.getId(), alliance);
    }

    @Override
    public void removeAlliance(final Alliance alliance) {
        this.getAlliances().remove(alliance.getId());
    }

    @Override
    public Optional<Alliance> getAllianceByClan(final Clan clan) {
        return Optional.ofNullable(this.getAlliances().get(clan.getId()));
    }

    @Override
    public boolean isAllianceByClan(final Clan clan) {
        return this.getAlliances().containsKey(clan.getId());
    }

    @Override
    public boolean isTrustedAllianceByClan(final Clan clan) {
        return this.getAllianceByClan(clan).map(Alliance::isTrusted).orElse(false);
    }

    @Override
    public boolean hasAlliances() {
        return !(this.getAlliances().isEmpty());
    }

    @Override
    public void addEnemy(final Enemy enemy) {
        this.getEnemies().put(enemy.getId(), enemy);
    }

    @Override
    public void removeEnemy(final Enemy enemy) {
        this.getEnemies().remove(enemy.getId());
    }

    @Override
    public Optional<Enemy> getEnemyByClan(final Clan clan) {
        return Optional.ofNullable(this.getEnemies().get(clan.getId()));
    }

    @Override
    public boolean isEnemyByClan(final Clan clan) {
        return this.getEnemies().containsKey(clan.getId());
    }

    @Override
    public boolean hasEnemies() {
        return !(this.getEnemies().isEmpty());
    }

    @Override
    public void addPillage(final Pillage pillage) {
        this.getPillages().put(pillage.getId(), pillage);
    }

    @Override
    public void removePillage(final Pillage pillage) {
        this.getPillages().remove(pillage.getId());
    }

    @Override
    public Optional<Pillage> getPillageByClan(final Clan clan) {
        return Optional.ofNullable(this.getPillages().get(clan.getId()));
    }

    @Override
    public boolean isPillageByClan(final Clan clan) {
        return this.getPillages().containsKey(clan.getId());
    }

    @Override
    public boolean hasPillages() {
        return !(this.getPillages().isEmpty());
    }

    @Override
    public boolean hasTerritory() {
        return !(this.getTerritory().isEmpty());
    }

    @Override
    public boolean isTerritory(final Chunk chunk) {
        return this.getTerritory().contains(chunk);
    }

    @Override
    public boolean hasHome() {
        return this.getHome() != null;
    }
}