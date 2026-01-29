package me.trae.clans.clan;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.data.*;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanProperty;
import me.trae.clans.clan.enums.RelationRequestType;
import me.trae.clans.clan.interfaces.IClan;
import me.trae.framework.database.domain.Domain;
import me.trae.framework.database.domain.data.DomainData;
import me.trae.framework.utility.objects.Chunk;
import me.trae.framework.utility.objects.Location;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
public class Clan implements IClan, Domain<ClanProperty> {

    private final UUID id;

    private String name;

    private final Map<UUID, Map<RelationRequestType, Request>> relationRequests;
    private final Map<UUID, Request> invitationRequests;

    private final LinkedHashMap<UUID, Member> members;
    private final LinkedHashMap<UUID, Alliance> alliances;
    private final LinkedHashMap<UUID, Enemy> enemies;
    private final LinkedHashMap<UUID, Pillage> pillages;

    private final List<Chunk> territory = new ArrayList<>();

    private long created;
    private UUID founder;
    private Location home;

    public Clan(final UUID id) {
        this.id = id;

        this.relationRequests = new HashMap<>();
        this.invitationRequests = new HashMap<>();
        this.members = new LinkedHashMap<>();
        this.alliances = new LinkedHashMap<>();
        this.enemies = new LinkedHashMap<>();
        this.pillages = new LinkedHashMap<>();
    }

    public Clan(final PlayerRef playerRef, final String name) {
        this(UUID.randomUUID());

        this.name = name;

        this.addMember(new Member(playerRef, MemberRole.LEADER));

        this.created = System.currentTimeMillis();
        this.founder = playerRef.getUuid();
    }

    public Clan(final DomainData<ClanProperty> data) {
        this(data.getId());

        this.name = data.get(String.class, ClanProperty.NAME);

        data.getMap(String.class, Map.class, ClanProperty.MEMBERS).forEach((id, map) -> this.addMember(new Member(new DomainData<>(id, (Map<String, Object>) map))));
        data.getMap(String.class, Map.class, ClanProperty.ALLIANCES).forEach((id, map) -> this.addAlliance(new Alliance(new DomainData<>(id, (Map<String, Object>) map))));
        data.getMap(String.class, Map.class, ClanProperty.ENEMIES).forEach((id, map) -> this.addEnemy(new Enemy(new DomainData<>(id, (Map<String, Object>) map))));
        data.getMap(String.class, Map.class, ClanProperty.PILLAGES).forEach((id, map) -> this.addPillage(new Pillage(new DomainData<>(id, (Map<String, Object>) map))));

        data.getList(Map.class, ClanProperty.TERRITORY).forEach(map -> this.addTerritory(new Chunk((Map<String, Object>) map)));

        this.created = data.get(Long.class, ClanProperty.CREATED);
        this.founder = UUID.fromString(data.get(String.class, ClanProperty.FOUNDER));
        this.home = data.contains(ClanProperty.HOME) ? new Location(data.getMap(String.class, Object.class, ClanProperty.HOME)) : null;
    }

    @Override
    public String getDisplayName() {
        return this.isAdmin() ? this.getName().replace("_", " ") : this.getName();
    }

    @Override
    public void addRelationRequest(final RelationRequestType relationRequestType, final Clan clan) {
        this.getRelationRequests().computeIfAbsent(clan.getId(), __ -> new HashMap<>()).put(relationRequestType, new Request(clan));
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
        this.getInvitationRequests().put(player.getUuid(), new Request(player));
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
    public void addTerritory(final Chunk chunk) {
        this.getTerritory().add(chunk);
    }

    @Override
    public void removeTerritory(final Chunk chunk) {
        this.getTerritory().remove(chunk);
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

    @Override
    public Object getValueByProperty(final ClanProperty clanProperty) {
        return switch (clanProperty) {
            case NAME -> this.getName();
            case MEMBERS -> this.getMembers().values().stream().collect(Collectors.toMap(Member::getIdString, Member::toMap));
            case ALLIANCES -> this.getAlliances().values().stream().collect(Collectors.toMap(Alliance::getIdString, Alliance::toMap));
            case ENEMIES -> this.getEnemies().values().stream().collect(Collectors.toMap(Enemy::getIdString, Enemy::toMap));
            case PILLAGES -> this.getPillages().values().stream().collect(Collectors.toMap(Pillage::getIdString, Pillage::toMap));
            case TERRITORY -> this.getTerritory().stream().map(Chunk::toMap).toList();
            case CREATED -> this.getCreated();
            case FOUNDER -> this.getFounder().toString();
            case HOME -> this.hasHome() ? this.getHome().toMap() : null;
        };
    }
}