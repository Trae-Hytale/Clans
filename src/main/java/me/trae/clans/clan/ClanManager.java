package me.trae.clans.clan;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.trae.di.annotations.method.ApplicationReady;
import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.hytale.framework.utility.UtilColor;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.hytale.framework.wrappers.Chunk;
import io.github.trae.hytale.framework.wrappers.Location;
import io.github.trae.utilities.UtilJava;
import io.github.trae.utilities.UtilString;
import io.github.trae.utilities.UtilTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.configs.ClansConfig;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.interfaces.IClanManager;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.clans.clan.storages.ClanChunkStorage;
import me.trae.clans.clan.storages.ClanIdStorage;
import me.trae.clans.clan.storages.ClanNameStorage;
import me.trae.clans.clan.storages.ClanPlayerStorage;

import java.awt.*;
import java.util.List;
import java.util.*;

@AllArgsConstructor
@Getter
@Service
public class ClanManager implements Manager<ClansPlugin>, IClanManager {

    private final ClanIdStorage clanIdStorage = new ClanIdStorage();
    private final ClanNameStorage clanNameStorage = new ClanNameStorage();
    private final ClanPlayerStorage clanPlayerStorage = new ClanPlayerStorage();
    private final ClanChunkStorage clanChunkStorage = new ClanChunkStorage();

    private final ClanRepository repository;

    private final ClansConfig config;

    @ApplicationReady
    public void onApplicationReady() {
        this.flushAllClans();

        this.repository.findManySynchronously(List.of()).forEach(this::addClan);
    }

    @Override
    public List<Clan> getClans() {
        return this.clanIdStorage.getValues();
    }

    @Override
    public void flushAllClans() {
        this.clanIdStorage.flush();
        this.clanNameStorage.flush();
        this.clanPlayerStorage.flush();
        this.clanChunkStorage.flush();
    }

    @Override
    public void addClan(final Clan clan) {
        this.clanIdStorage.index(clan);
        this.clanNameStorage.index(clan);
        this.clanPlayerStorage.index(clan);
        this.clanChunkStorage.index(clan);
    }

    @Override
    public void removeClan(final Clan clan) {
        this.clanIdStorage.unIndex(clan);
        this.clanNameStorage.unIndex(clan);
        this.clanPlayerStorage.unIndex(clan);
        this.clanChunkStorage.unIndex(clan);
    }

    @Override
    public Optional<Clan> getClanById(final UUID id) {
        return this.clanIdStorage.get(id);
    }

    @Override
    public Optional<Clan> getClanByName(final String name) {
        return this.clanNameStorage.get(name);
    }

    @Override
    public Optional<Clan> getClanByPlayerId(final UUID playerId) {
        return this.clanPlayerStorage.get(playerId);
    }

    @Override
    public Optional<Clan> getClanByPlayer(final PlayerRef playerRef) {
        return this.getClanByPlayerId(playerRef.getUuid());
    }

    @Override
    public Optional<Clan> getClanByChunk(final Chunk chunk) {
        return this.clanChunkStorage.get(chunk);
    }

    @Override
    public Optional<Clan> getClanByLocation(final Location location) {
        return this.getClanByChunk(location.getChunk());
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
    public ClanRelation getClanRelationByPlayer(final PlayerRef playerRef, final PlayerRef targetPlayerRef) {
        return this.getClanRelationByClan(this.getClanByPlayer(playerRef).orElse(null), this.getClanByPlayer(targetPlayerRef).orElse(null));
    }

    @Override
    public String getClanName(final ClanRelation clanRelation, final Clan clan) {
        return "";
    }

    @Override
    public String getClanFullName(final ClanRelation clanRelation, final Clan clan) {
        return "";
    }

    @Override
    public String getClanShortName(final ClanRelation clanRelation, final Clan clan) {
        return "";
    }

    @Override
    public void showClanInformation(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        final LinkedHashMap<String, String> informationMap = UtilJava.createMap(new LinkedHashMap<>(), map -> {
            map.put("Age", "<yellow>%s</yellow>".formatted(UtilTime.getTime(System.currentTimeMillis() - targetClan.getCreatedAt())));

            map.put("Territory", "<yellow>%s/%s</yellow>".formatted(targetClan.getTerritory().size(), this.getMaxClaimLimit(targetClan)));

            map.put("Allies", String.join("<gray>, ", UtilJava.createCollection(new ArrayList<String>(), list -> {
                for (final UUID id : targetClan.getAlliances().keySet()) {
                    this.getClanById(id).ifPresent(allianceClan -> {
                        final ClanRelation clanRelation = this.getClanRelationByClan(playerClan, allianceClan);

                        list.add(UtilColor.serialize(clanRelation.getSuffix(), allianceClan.getDisplayName()));
                    });
                }
            })));

            map.put("Enemies", String.join("<gray>, ", UtilJava.createCollection(new ArrayList<String>(), list -> {
                for (final UUID id : targetClan.getEnemies().keySet()) {
                    this.getClanById(id).ifPresent(enemyClan -> {
                        final ClanRelation clanRelation = this.getClanRelationByClan(playerClan, enemyClan);

                        list.add(UtilColor.serialize(clanRelation.getSuffix(), enemyClan.getDisplayName()));
                    });
                }
            })));

            map.put("Pillages", String.join("<gray>, ", UtilJava.createCollection(new ArrayList<String>(), list -> {
                for (final UUID id : targetClan.getPillages().keySet()) {
                    this.getClanById(id).ifPresent(pillageClan -> {
                        final ClanRelation clanRelation = this.getClanRelationByClan(playerClan, pillageClan);

                        list.add(UtilColor.serialize(clanRelation.getSuffix(), pillageClan.getDisplayName()));
                    });
                }
            })));

            map.put("Members", String.join("<gray>, ", UtilJava.createCollection(new ArrayList<String>(), list -> {
                for (final Member member : targetClan.getMembers().values()) {
                    final PlayerRef memberPlayerRef = Universe.get().getPlayer(member.getId());
                    if (memberPlayerRef == null) {
                        continue;
                    }

                    final Color color = memberPlayerRef.isValid() ? ChatColor.GREEN.getColor() : ChatColor.RED.getColor();

                    final String memberName = UtilColor.serialize(color, memberPlayerRef.getUsername());

                    list.add("<yellow>%s</yellow><gray>.</gray>%s".formatted(member.getRole().getPrefix(), memberName));
                }
            })));
        });

        UtilMessage.message(playerRef, "Clans", "%s Information:".formatted(UtilColor.serialize(this.getClanRelationByClan(playerClan, targetClan).getSuffix(), targetClan.getDisplayName())));

        for (final Map.Entry<String, String> entry : informationMap.entrySet()) {
            UtilMessage.message(playerRef, UtilString.pair(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void disbandClan(final Clan clan) {
        for (final Clan targetClan : this.getClans()) {
            targetClan.getAllianceByClan(clan).ifPresent(alliance -> {
                targetClan.removeAlliance(alliance);
                this.repository.update(targetClan, ClanProperty.ALLIANCES);
            });

            targetClan.getEnemyByClan(clan).ifPresent(enemy -> {
                targetClan.removeEnemy(enemy);
                this.repository.update(targetClan, ClanProperty.ENEMIES);
            });

            targetClan.getPillageByClan(clan).ifPresent(pillage -> {
                targetClan.removePillage(pillage);
                this.repository.update(targetClan, ClanProperty.PILLAGES);
            });
        }

        this.removeClan(clan);
        this.repository.delete(clan);
    }

    @Override
    public int getMaxClaimLimit(final Clan clan) {
        return Math.min(this.config.getTerritory().maxClaimLimit(), 3 + clan.getMembers().size());
    }
}