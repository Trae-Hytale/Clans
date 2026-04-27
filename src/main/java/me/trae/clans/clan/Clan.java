package me.trae.clans.clan;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.database.domain.data.DomainData;
import io.github.trae.database.domain.models.Domain;
import io.github.trae.hytale.framework.wrappers.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.trae.clans.clan.data.Alliance;
import me.trae.clans.clan.data.Enemy;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.data.Pillage;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.data.properties.AllianceProperty;
import me.trae.clans.clan.data.properties.EnemyProperty;
import me.trae.clans.clan.data.properties.MemberProperty;
import me.trae.clans.clan.data.properties.PillageProperty;
import me.trae.clans.clan.interfaces.IClan;
import me.trae.clans.clan.properties.ClanProperty;

import java.util.*;

@RequiredArgsConstructor
@Getter
@Setter
public class Clan implements Domain<ClanProperty>, IClan {

    private final UUID id;

    private String name;

    private final List<Chunk> territory = new ArrayList<>();

    private final LinkedHashMap<UUID, Member> members = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, Alliance> alliances = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, Enemy> enemies = new LinkedHashMap<>();
    private final LinkedHashMap<UUID, Pillage> pillages = new LinkedHashMap<>();

    private UUID founder;
    private long createdAt;

    public Clan(final DomainData<ClanProperty> domainData) {
        this(domainData.getIdentifier());

        this.name = domainData.get(String.class, ClanProperty.NAME);
        this.founder = domainData.get(UUID.class, ClanProperty.FOUNDER);

        this.territory.addAll(domainData.getList(LinkedHashMap.class, ClanProperty.TERRITORY).stream().map(Chunk::deserialize).toList());

        this.members.putAll(domainData.<MemberProperty, Member>getSubDomainMap(ClanProperty.MEMBERS, Member::new));
        this.alliances.putAll(domainData.<AllianceProperty, Alliance>getSubDomainMap(ClanProperty.ALLIANCES, Alliance::new));
        this.enemies.putAll(domainData.<EnemyProperty, Enemy>getSubDomainMap(ClanProperty.ENEMIES, Enemy::new));
        this.pillages.putAll(domainData.<PillageProperty, Pillage>getSubDomainMap(ClanProperty.PILLAGES, Pillage::new));

        this.createdAt = domainData.get(Long.class, ClanProperty.CREATED_AT);
    }

    public Clan(final PlayerRef playerRef, final String name) {
        this(UUID.randomUUID());

        this.name = name;
        this.members.put(playerRef.getUuid(), new Member(playerRef, MemberRole.LEADER));
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public Object getValueByProperty(final ClanProperty clanProperty) {
        return switch (clanProperty) {
            case NAME -> this.getName();
            case TERRITORY -> this.getTerritory().stream().map(Chunk::serialize).toList();
            case MEMBERS -> this.getMembers();
            case ALLIANCES -> this.getAlliances();
            case ENEMIES -> this.getEnemies();
            case PILLAGES -> this.getPillages();
            case FOUNDER -> this.getFounder();
            case CREATED_AT -> this.getCreatedAt();
        };
    }

    @Override
    public String getDisplayName() {
        return this.getName().replace("_", " ");
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
    public boolean isTerritoryByChunk(final Chunk chunk) {
        return this.getTerritory().contains(chunk);
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
    public Optional<Member> getMemberById(final UUID id) {
        return Optional.ofNullable(this.getMembers().get(id));
    }

    @Override
    public Optional<Member> getMemberByPlayer(final PlayerRef playerRef) {
        return this.getMemberById(playerRef.getUuid());
    }

    @Override
    public boolean isMemberById(final UUID id) {
        return this.getMembers().containsKey(id);
    }

    @Override
    public boolean isMemberByPlayer(final PlayerRef playerRef) {
        return this.isMemberById(playerRef.getUuid());
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
    public Optional<Alliance> getAllianceById(final UUID id) {
        return Optional.ofNullable(this.getAlliances().get(id));
    }

    @Override
    public Optional<Alliance> getAllianceByClan(final Clan clan) {
        return this.getAllianceById(clan.getId());
    }

    @Override
    public boolean isAllianceById(final UUID id) {
        return this.getAlliances().containsKey(id);
    }

    @Override
    public boolean isAllianceByClan(final Clan clan) {
        return this.isAllianceById(clan.getId());
    }

    @Override
    public boolean isTrustedAllianceByClan(final Clan clan) {
        return this.getAllianceByClan(clan).map(Alliance::isTrusted).orElse(false);
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
    public Optional<Enemy> getEnemyById(final UUID id) {
        return Optional.ofNullable(this.getEnemies().get(id));
    }

    @Override
    public Optional<Enemy> getEnemyByClan(final Clan clan) {
        return this.getEnemyById(clan.getId());
    }

    @Override
    public boolean isEnemyById(final UUID id) {
        return this.getEnemies().containsKey(id);
    }

    @Override
    public boolean isEnemyByClan(final Clan clan) {
        return this.isEnemyById(clan.getId());
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
    public Optional<Pillage> getPillageById(final UUID id) {
        return Optional.ofNullable(this.getPillages().get(id));
    }

    @Override
    public Optional<Pillage> getPillageByClan(final Clan clan) {
        return this.getPillageById(clan.getId());
    }

    @Override
    public boolean isPillageById(final UUID id) {
        return this.getPillages().containsKey(id);
    }

    @Override
    public boolean isPillageByClan(final Clan clan) {
        return this.isPillageById(clan.getId());
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof final Clan clan && this.getId().equals(clan.getId());
    }
}