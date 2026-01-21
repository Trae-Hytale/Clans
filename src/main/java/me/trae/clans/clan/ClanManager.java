package me.trae.clans.clan;

import com.hypixel.hytale.math.vector.Location;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.AllArgsConstructor;
import me.trae.clans.Clans;
import me.trae.clans.clan.data.Alliance;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.interfaces.IClanManager;
import me.trae.clans.clan.wrappers.ClanUpdater;
import me.trae.core.client.Client;
import me.trae.core.client.ClientManager;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Manager;
import me.trae.framework.updater.annotations.Update;
import me.trae.framework.updater.interfaces.Updater;
import me.trae.framework.utility.UtilColor;
import me.trae.framework.utility.UtilJava;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.enums.ChatColor;
import me.trae.framework.utility.java.ConcurrentLinkedHashMap;
import me.trae.framework.utility.objects.Chunk;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor
@Component
public class ClanManager implements Manager<Clans>, IClanManager, Updater {

    private final ConcurrentLinkedHashMap<UUID, Clan> CLAN_BY_ID_MAP = new ConcurrentLinkedHashMap<>();
    private final ConcurrentMap<String, Clan> CLAN_BY_NAME_MAP = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Clan> CLAN_BY_PLAYER_ID_MAP = new ConcurrentHashMap<>();
    private final ConcurrentMap<Chunk, Clan> CLAN_BY_CHUNK_MAP = new ConcurrentHashMap<>();

    private final ClientManager clientManager;

    private final List<ClanUpdater> clanUpdaterList;

    @Update(delay = 500L)
    public void onUpdater() {
        for (final Clan clan : this.CLAN_BY_ID_MAP.values()) {
            this.clanUpdaterList.forEach(clanUpdater -> clanUpdater.onUpdater(clan));
        }
    }

    @Override
    public void updateNameInClanCache(final String previousName, final Clan clan) {
        this.CLAN_BY_NAME_MAP.remove(previousName);
        this.CLAN_BY_NAME_MAP.put(clan.getName(), clan);
    }

    @Override
    public void updatePlayerInClanCache(final UUID id, final Clan clan) {
        if (clan == null) {
            this.CLAN_BY_PLAYER_ID_MAP.remove(id);
            return;
        }

        this.CLAN_BY_PLAYER_ID_MAP.put(id, clan);
    }

    @Override
    public void updateChunkInClanCache(final Chunk chunk, final Clan clan) {
        if (clan == null) {
            this.CLAN_BY_CHUNK_MAP.remove(chunk);
            return;
        }

        this.CLAN_BY_CHUNK_MAP.put(chunk, clan);
    }

    @Override
    public List<Clan> getClans() {
        return this.CLAN_BY_ID_MAP.values().stream().toList();
    }

    @Override
    public void addClan(final Clan clan) {
        this.CLAN_BY_ID_MAP.put(clan.getId(), clan);
        this.CLAN_BY_NAME_MAP.put(clan.getName(), clan);

        if (clan.hasMembers()) {
            clan.getMembers().keySet().forEach(id -> this.CLAN_BY_PLAYER_ID_MAP.put(id, clan));
        }

        if (clan.hasTerritory()) {
            clan.getTerritory().forEach(chunk -> this.CLAN_BY_CHUNK_MAP.put(chunk, clan));
        }
    }

    @Override
    public void removeClan(final Clan clan) {
        this.CLAN_BY_ID_MAP.remove(clan.getId());
        this.CLAN_BY_NAME_MAP.remove(clan.getName());

        if (clan.hasMembers()) {
            clan.getMembers().keySet().forEach(this.CLAN_BY_PLAYER_ID_MAP::remove);
        }

        if (clan.hasTerritory()) {
            clan.getTerritory().forEach(this.CLAN_BY_CHUNK_MAP::remove);
        }
    }

    @Override
    public Optional<Clan> getClanById(final UUID id) {
        return Optional.ofNullable(this.CLAN_BY_ID_MAP.get(id));
    }

    @Override
    public Optional<Clan> getClanByName(final String name) {
        return Optional.ofNullable(this.CLAN_BY_NAME_MAP.get(name));
    }

    @Override
    public Optional<Clan> getClanByPlayerId(final UUID id) {
        return Optional.ofNullable(this.CLAN_BY_PLAYER_ID_MAP.get(id));
    }

    @Override
    public Optional<Clan> getClanByPlayer(final PlayerRef player) {
        return this.getClanByPlayerId(player.getUuid());
    }

    @Override
    public Optional<Clan> getClanByChunk(final Chunk chunk) {
        return Optional.ofNullable(this.CLAN_BY_CHUNK_MAP.get(chunk));
    }

    @Override
    public Optional<Clan> getClanByLocation(final Location location) {
        return this.getClanByChunk(Chunk.of(location));
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
    public ClanRelation getClanRelationByPlayer(final PlayerRef player, final PlayerRef target) {
        return this.getClanRelationByClan(this.getClanByPlayer(player).orElse(null), this.getClanByPlayer(target).orElse(null));
    }

    @Override
    public String getClanFullName(final ClanRelation clanRelation, final Clan clan) {
        return UtilColor.serialize(clanRelation.getSuffix(), "%s %s".formatted(clan.getType(), clan.getName()));
    }

    @Override
    public String getClanShortName(final ClanRelation clanRelation, final Clan clan) {
        return UtilColor.serialize(clanRelation.getSuffix(), clan.getName());
    }

    @Override
    public String getClanName(final ClanRelation clanRelation, final Clan clan) {
        return clan.isAdmin() ? this.getClanShortName(clanRelation, clan) : this.getClanFullName(clanRelation, clan);
    }

    @Override
    public void messageClan(final Clan clan, final String prefix, final String message, final List<UUID> ignore) {
        UtilMessage.message(clan.getMembersAsPlayers(), prefix, message, ignore);
    }

    @Override
    public void messageAllies(final Clan clan, final String prefix, final String message, final List<UUID> ignore) {
        for (final Alliance alliance : clan.getAlliances().values()) {
            this.getClanById(alliance.getId()).ifPresent(allianceClan -> this.messageClan(allianceClan, prefix, message, ignore));
        }
    }

    @Override
    public Optional<Clan> searchClan(final IMessageReceiver messageReceiver, final String name, final boolean inform) {
        final List<Predicate<Clan>> predicateList = List.of(
                clan -> clan.getName().equalsIgnoreCase(name),
                clan -> clan.getName().toLowerCase().contains(name.toLowerCase())
        );

        final Function<Clan, String> function = (clan -> {
            Color color = ChatColor.YELLOW.getColor();

            if (messageReceiver instanceof final PlayerRef playerRef) {
                color = this.getClanRelationByClan(this.getClanByPlayer(playerRef).orElse(null), clan).getSuffix();
            }

            return UtilColor.serialize(color, clan.getName());
        });

        final Consumer<List<Clan>> consumer = (list -> {
            this.clientManager.searchClient(messageReceiver, name, false).flatMap(client -> this.getClanByPlayerId(client.getId())).ifPresent(clientClan -> {
                if (list.contains(clientClan)) {
                    return;
                }

                list.add(clientClan);
            });
        });

        return UtilJava.search(this.getClans(), predicateList, consumer, function, "Clan Search", messageReceiver, name, inform);
    }

    @Override
    public Optional<Member> searchMember(final Clan clan, final IMessageReceiver messageReceiver, final String name, final boolean inform) {
        final List<Client> clientList = clan.getMembers().keySet().stream().map(this.clientManager::getClientByPlayerId).flatMap(Optional::stream).toList();

        final List<Predicate<Client>> predicateList = List.of(
                client -> client.getName().equalsIgnoreCase(name),
                client -> client.getName().toLowerCase().contains(name.toLowerCase())
        );

        final Function<Client, String> function = client -> UtilColor.serialize(ChatColor.AQUA.getColor(), client.getName());

        final Optional<Client> clientOptional = UtilJava.search(clientList, predicateList, null, function, "Member Search", messageReceiver, name, inform);
        if (clientOptional.isEmpty()) {
            return Optional.empty();
        }

        return clan.getMemberByPlayerId(clientOptional.get().getId());
    }
}