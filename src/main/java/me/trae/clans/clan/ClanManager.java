package me.trae.clans.clan;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.receiver.IMessageReceiver;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import io.github.trae.di.annotations.method.ApplicationReady;
import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.utility.UtilColor;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilSearch;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import io.github.trae.hytale.framework.wrappers.Chunk;
import io.github.trae.hytale.framework.wrappers.Location;
import io.github.trae.utilities.UtilJava;
import io.github.trae.utilities.UtilString;
import io.github.trae.utilities.UtilTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.configs.EnergyConfig;
import me.trae.clans.clan.configs.SquadConfig;
import me.trae.clans.clan.configs.TerritoryConfig;
import me.trae.clans.clan.data.Enemy;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.data.Pillage;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.enums.ClansChatChannel;
import me.trae.clans.clan.enums.InteractType;
import me.trae.clans.clan.interfaces.IClanManager;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.clans.clan.storages.ClanChunkStorage;
import me.trae.clans.clan.storages.ClanIdStorage;
import me.trae.clans.clan.storages.ClanNameStorage;
import me.trae.clans.clan.storages.ClanPlayerStorage;
import me.trae.clans.pillage.configs.PillageConfig;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.client.Client;
import me.trae.core.client.ClientManager;
import me.trae.core.cooldown.CooldownManager;
import me.trae.core.gamer.GamerManager;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Function;

@AllArgsConstructor
@Getter
@Service
public class ClanManager implements Manager<ClansPlugin>, IClanManager, EventListener {

    public static final Function<Clan, String> CHUNK_OUTLINE_BLOCK_RESTORE_NAME_FORMATTER = clan -> "CLAN:%s".formatted(clan.getId().toString());

    private final ClanIdStorage clanIdStorage = new ClanIdStorage();
    private final ClanNameStorage clanNameStorage = new ClanNameStorage();
    private final ClanPlayerStorage clanPlayerStorage = new ClanPlayerStorage();
    private final ClanChunkStorage clanChunkStorage = new ClanChunkStorage();

    private final ClanRepository repository;

    private final ClientManager clientManager;
    private final GamerManager coreGamerManager;
    private final CooldownManager cooldownManager;
    private final BlockRestoreManager blockRestoreManager;

    private final EnergyConfig energyConfig;
    private final PillageConfig pillageConfig;
    private final SquadConfig squadConfig;
    private final TerritoryConfig territoryConfig;

    @ApplicationReady
    public void onApplicationReady() {
        this.flushAllClans();

        int count = 0;

        for (final Clan clan : this.repository.findManySynchronously(List.of())) {
            this.addClan(clan);
            count++;
        }

        UtilMessage.log("Database", "Loaded <yellow>%s</yellow> Clans.".formatted(count));
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
    public Optional<Clan> searchClan(final IMessageReceiver messageReceiver, final String name, final boolean inform) {
        return UtilSearch.search(
                this.getClans(),
                clan -> clan.getName().equalsIgnoreCase(name),
                clan -> clan.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)),
                list -> {
                    if (messageReceiver instanceof final PlayerRef playerRef) {
                        this.getClientManager().searchClient(playerRef, name, false).flatMap(client -> this.getClanByPlayerId(client.getId())).ifPresent(clientClan -> {
                            if (list.contains(clientClan)) {
                                return;
                            }

                            list.add(clientClan);
                        });
                    }
                },
                string -> UtilColor.serialize(ChatColor.YELLOW.getColor(), string),
                clan -> {
                    ClanRelation clanRelation = ClanRelation.NEUTRAL;

                    if (messageReceiver instanceof final PlayerRef playerRef) {
                        clanRelation = this.getClanRelationByClan(this.getClanByPlayer(playerRef).orElse(null), clan);
                    }

                    return UtilColor.serialize(clanRelation.getSuffix(), clan.getDisplayName());
                },
                "Clan Search",
                messageReceiver,
                name,
                inform
        );
    }

    @Override
    public Optional<Client> searchMember(final Clan clan, final IMessageReceiver messageReceiver, final String name, final boolean inform) {
        final List<Client> clientList = clan.getMembers().values().stream().map(member -> this.getClientManager().getClientById(member.getId()).orElse(null)).toList();

        return UtilSearch.search(
                clientList,
                memberClient -> memberClient.getName().equalsIgnoreCase(name),
                memberClient -> memberClient.getName().toLowerCase(Locale.ROOT).contains(name.toLowerCase(Locale.ROOT)),
                null,
                string -> UtilColor.serialize(ClanRelation.SELF.getSuffix(), string),
                Client::getName,
                "Member Search",
                messageReceiver,
                name,
                inform
        );
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
    public void showClanInformation(final PlayerRef playerRef, final Client playerClient, final Clan playerClan, final Clan targetClan) {
        final LinkedHashMap<String, String> informationMap = UtilJava.createMap(new LinkedHashMap<>(), map -> {
            if (playerClient.isAdministrating()) {
                map.put("Admin", targetClan.isAdmin() ? "<green>Yes</green>" : "<red>No</red>");

                map.put("Founder", this.getClientManager().getClientById(targetClan.getFounder()).map(client -> "<yellow>%s</yellow>".formatted(client.getName())).orElse("null"));
            }

            map.put("Age", "<yellow>%s</yellow>".formatted(UtilTime.getTime(System.currentTimeMillis() - targetClan.getCreatedAt())));

            map.put("Territory", "<yellow>%s/%s</yellow>".formatted(targetClan.getTerritory().size(), this.getMaxClaimLimit(targetClan)));

            if (this.energyConfig.isEnabled()) {
                map.put("Energy", "<green>%s</green>".formatted(targetClan.getFormattedEnergyRemaining()));
            }

            if (targetClan.equals(playerClan)) {
                map.put("Home", targetClan.hasHome() ? targetClan.getFormattedHomeLocation() : "<red>Not set</red>");
            }

            map.put("Allies", String.join("<gray>, ", UtilJava.createCollection(new ArrayList<String>(), list -> {
                for (final UUID id : targetClan.getAlliances().keySet()) {
                    this.getClanById(id).ifPresent(allianceClan -> {
                        final ClanRelation clanRelation = this.getClanRelationByClan(playerClan, allianceClan);

                        list.add(this.getClanShortName(clanRelation, allianceClan));
                    });
                }
            })));

            map.put("Enemies", String.join("<gray>, ", UtilJava.createCollection(new ArrayList<String>(), list -> {
                for (final UUID id : targetClan.getEnemies().keySet()) {
                    this.getClanById(id).ifPresent(enemyClan -> {
                        final ClanRelation clanRelation = this.getClanRelationByClan(playerClan, enemyClan);

                        list.add(this.getClanShortName(clanRelation, enemyClan));
                    });
                }
            })));

            map.put("Pillages", String.join("<gray>, ", UtilJava.createCollection(new ArrayList<String>(), list -> {
                for (final UUID id : targetClan.getPillages().keySet()) {
                    this.getClanById(id).ifPresent(pillageClan -> {
                        final ClanRelation clanRelation = this.getClanRelationByClan(playerClan, pillageClan);

                        list.add(this.getClanShortName(clanRelation, pillageClan));
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

        UtilMessage.message(playerRef, "Clans", "%s Information:".formatted(this.getClanShortName(this.getClanRelationByClan(playerClan, targetClan), targetClan)));

        for (final Map.Entry<String, String> entry : informationMap.entrySet()) {
            UtilMessage.message(playerRef, UtilString.pair(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public void removeChatChannel(final PlayerRef playerRef) {
        if (playerRef == null) {
            return;
        }

        this.coreGamerManager.getGamerByPlayer(playerRef).ifPresent(gamer -> {
            for (final ClansChatChannel chatChannel : ClansChatChannel.values()) {
                if (!(gamer.getChatChannel().equals(chatChannel))) {
                    continue;
                }

                gamer.resetChatChannel();
                break;
            }
        });
    }

    @Override
    public void disbandClan(final Clan clan) {
        for (final Member member : clan.getMembers().values()) {
            this.removeChatChannel(member.getPlayerRef());
        }

        this.blockRestoreManager.unOutlineAllChunks(clan.getTerritory(), CHUNK_OUTLINE_BLOCK_RESTORE_NAME_FORMATTER.apply(clan));

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
    public void messageClan(final Clan clan, final String prefix, final String message, final List<UUID> ignored) {
        for (final Member member : clan.getMembers().values()) {
            if (ignored != null && ignored.contains(member.getId())) {
                continue;
            }

            UtilMessage.message(member.getPlayerRef(), prefix, message);
        }
    }

    @Override
    public String getClanName(final ClanRelation clanRelation, final Clan clan) {
        return clan.isAdmin() ? this.getClanShortName(clanRelation, clan) : this.getClanFullName(clanRelation, clan);
    }

    @Override
    public String getClanFullName(final ClanRelation clanRelation, final Clan clan) {
        Color color = clanRelation.getSuffix();

        if (clan.isAdmin() && !(clan.isOutskirts()) && clanRelation != ClanRelation.SELF) {
            color = ChatColor.WHITE.getColor();
        }

        return UtilColor.serialize(color, "%s %s".formatted(clan.getType(), clan.getDisplayName()));
    }

    @Override
    public String getClanShortName(final ClanRelation clanRelation, final Clan clan) {
        Color color = clanRelation.getSuffix();

        if (clan.isAdmin() && !(clan.isOutskirts()) && clanRelation != ClanRelation.SELF) {
            color = ChatColor.WHITE.getColor();
        }

        return UtilColor.serialize(color, clan.getDisplayName());
    }

    @Override
    public String getPlayerName(final ClanRelation clanRelation, final String playerName) {
        return UtilColor.serialize(clanRelation.getSuffix(), playerName);
    }

    @Override
    public String getPlayerName(final ClanRelation clanRelation, final PlayerRef playerRef) {
        return this.getPlayerName(clanRelation, playerRef.getUsername());
    }

    @Override
    public Message getTerritoryClanNameForChat(final Clan playerClan, final Clan territoryClan, final BlockLocation blockLocation) {
        String name = "Wilderness";
        Color color = ChatColor.YELLOW.getColor();
        Message suffix = null;

        if (territoryClan != null) {
            name = territoryClan.getDisplayName();
            color = this.getClanRelationByClan(playerClan, territoryClan).getSuffix();

            if (territoryClan.isAdmin()) {
                if (!(territoryClan.isOutskirts())) {
                    color = ChatColor.WHITE.getColor();
                }

                if (territoryClan.isSafe()) {
                    suffix = Message.raw("Safe").color(ChatColor.AQUA.getColor());
                }
            } else if (playerClan != null) {
                if (playerClan.isTrustedAllianceByClan(territoryClan)) {
                    suffix = Message.raw("Trusted").color(ChatColor.YELLOW.getColor());
                } else if (playerClan.isEnemyByClan(territoryClan)) {
                    final Optional<Enemy> playerClanEnemyOptional = playerClan.getEnemyByClan(territoryClan);
                    final Optional<Enemy> territoryClanEnemyOptional = territoryClan.getEnemyByClan(playerClan);

                    if (playerClanEnemyOptional.isPresent() && territoryClanEnemyOptional.isPresent()) {
                        final Message playerClanEnemyPoints = Message.raw(String.valueOf(playerClanEnemyOptional.get().getPoints())).color(ChatColor.GREEN.getColor());
                        final Message territoryClanEnemyPoints = Message.raw(String.valueOf(territoryClanEnemyOptional.get().getPoints())).color(ChatColor.RED.getColor());

                        suffix = Message.join(playerClanEnemyPoints, Message.raw(":").color(ChatColor.GRAY.getColor()), territoryClanEnemyPoints);
                    }
                } else if (playerClan.isPillageByClan(territoryClan)) {
                    final Optional<Pillage> pillageOptional = playerClan.getPillageByClan(territoryClan);
                    if (pillageOptional.isPresent()) {
                        final long remaining = UtilTime.getRemaining(pillageOptional.get().getCreatedAt(), this.pillageConfig.getDuration());

                        suffix = Message.raw(UtilTime.getTime(remaining));
                    }
                }
            }
        }

        final Message message = Message.raw(name).color(color);

        if (suffix != null) {
            message.insert(Message.raw(" (").color(ChatColor.GRAY.getColor())).insert(suffix).insert(Message.raw(")").color(ChatColor.GRAY.getColor()));
        }

        return message;
    }

    @Override
    public Message getTerritoryClanNameForTitle(final Clan playerClan, final Clan territoryClan, final BlockLocation blockLocation) {
        String name = "Wilderness";
        Color color = ChatColor.YELLOW.getColor();

        if (territoryClan != null) {
            name = territoryClan.getDisplayName();
            color = this.getClanRelationByClan(playerClan, territoryClan).getSuffix();

            if (territoryClan.isAdmin()) {
                if (!(territoryClan.isOutskirts())) {
                    color = ChatColor.WHITE.getColor();
                }
            }
        }

        return Message.raw(name).color(color);
    }

    @Override
    public Message getTerritoryClanNameForSidebar(final Clan playerClan, final Clan territoryClan, final BlockLocation blockLocation) {
        String name = "Wilderness";
        Color color = ChatColor.GRAY.getColor();

        if (territoryClan != null) {
            name = territoryClan.getDisplayName();
            color = this.getClanRelationByClan(playerClan, territoryClan).getSuffix();

            if (territoryClan.isAdmin()) {
                if (!(territoryClan.isOutskirts())) {
                    color = ChatColor.WHITE.getColor();
                }
            }
        }

        return Message.raw(name).color(color);
    }

    @Override
    public Color getClanMapColor(final Clan playerClan, final Clan territoryClan) {
        Color color = this.getClanRelationByClan(playerClan, territoryClan).getSuffix();

        if (territoryClan.isAdmin()) {
            color = ChatColor.WHITE.getColor();
        }

        return color;
    }

    @Override
    public int getMaxClaimLimit(final Clan clan) {
        final int territoryCount = clan.getMembers().size();

        return Math.min(this.territoryConfig.getMaxClaimLimit(), territoryCount);
    }

    @Override
    public boolean isTerritoryFull(final Clan clan) {
        final int territoryCount = clan.getMembers().size();

        return clan.getTerritory().size() >= territoryCount;
    }

    @Override
    public int getMaxSquadLimit(final Clan clan) {
        final int squadCount = clan.getMembers().size() + clan.getAlliances().size();

        return Math.min(this.squadConfig.getMaxLimit(), squadCount);
    }

    @Override
    public boolean isSquadFull(final Clan clan) {
        final int squadCount = clan.getMembers().size() + clan.getAlliances().size();

        return squadCount >= this.squadConfig.getMaxLimit();
    }

    @Override
    public boolean canInteract(final PlayerRef playerRef, final Clan playerClan, final Clan territoryClan, final InteractType interactType) {
        if (territoryClan != null) {
            if (this.clientManager.getClientByPlayer(playerRef).map(Client::isAdministrating).orElse(false)) {
                return true;
            }

            if (playerClan != null) {
                if (territoryClan.equals(playerClan)) {
                    if (interactType == InteractType.GATEWAY_INTERACT) {
                        return true;
                    }

                    return territoryClan.getMemberByPlayer(playerRef).map(member -> member.getRole() != MemberRole.RECRUIT).orElse(false);
                }

                if (territoryClan.isTrustedAllianceByClan(playerClan)) {
                    return interactType == InteractType.GATEWAY_INTERACT;
                }

                if (playerClan.isPillageByClan(territoryClan)) {
                    return true;
                }
            }

            return false;
        }

        return true;
    }

    @Override
    public boolean canHurt(final PlayerRef damagee, final PlayerRef damager) {
        if (damagee != null && damager != null) {
            final Optional<Clan> damagerClanOptional = this.getClanByPlayer(damagee);
            final Optional<Clan> damageeClanOptional = this.getClanByPlayer(damager);

            if (damagerClanOptional.isPresent() && damageeClanOptional.isPresent()) {
                final Clan damagerClan = damagerClanOptional.get();
                final Clan damageeClan = damageeClanOptional.get();

                if (damagerClan.equals(damageeClan)) {
                    return false;
                }

                if (damagerClan.isAllianceByClan(damageeClan)) {
                    return false;
                }
            }
        }

        return true;
    }
}