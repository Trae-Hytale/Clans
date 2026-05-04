package me.trae.clans.clan.systems.territory;

import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.system.CustomEntityTickingSystem;
import io.github.trae.hytale.framework.system.data.SystemContext;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.TerritoryChangeEvent;
import me.trae.clans.clan.events.TerritoryEnterEvent;
import me.trae.clans.clan.events.TerritoryExitEvent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TerritoryTickingSystem extends CustomEntityTickingSystem implements Module<ClansPlugin, ClanManager>, Listener {

    private static final UUID WILDERNESS_ID = new UUID(0L, 0L);

    private final ConcurrentHashMap<UUID, UUID> playerLastTerritoryMap = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }

    @Override
    public void onTick(final float v, final SystemContext<EntityStore> systemContext) {
        final PlayerRef playerRef = systemContext.getComponent(PlayerRef.getComponentType());
        if (playerRef == null) {
            return;
        }

        final Player player = systemContext.getComponent(Player.getComponentType());
        if (player == null) {
            return;
        }

        final World world = player.getWorld();
        if (world == null) {
            return;
        }

        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

        final Chunk chunk = Chunk.of(world, playerRef.getTransform().getPosition());

        final Optional<Clan> territoryClanOptional = this.getManager().getClanByChunk(chunk);

        final UUID currentTerritoryKey = territoryClanOptional.map(Clan::getId).orElse(WILDERNESS_ID);
        final UUID previousTerritoryKey = this.playerLastTerritoryMap.get(playerRef.getUuid());

        if (currentTerritoryKey.equals(previousTerritoryKey)) {
            return;
        }

        this.playerLastTerritoryMap.put(playerRef.getUuid(), currentTerritoryKey);

        final BlockLocation blockLocation = BlockLocation.of(world, playerRef.getTransform().getPosition().toVector3i());

        EventTitleUtil.hideEventTitleFromPlayer(playerRef, 0.0F);
        EventTitleUtil.showEventTitleToPlayer(playerRef, this.getManager().getTerritoryClanNameForTitle(playerClanOptional.orElse(null), territoryClanOptional.orElse(null), blockLocation), Message.raw("Territory").color(ChatColor.RED.getColor()).bold(true), false);

        UtilMessage.message(playerRef, "Territory", this.getManager().getTerritoryClanNameForChat(playerClanOptional.orElse(null), territoryClanOptional.orElse(null), blockLocation));

        UtilEvent.dispatch(new TerritoryChangeEvent(playerRef));

        territoryClanOptional.ifPresent(clan -> UtilEvent.dispatch(new TerritoryEnterEvent(playerRef, clan)));

        if (previousTerritoryKey != null) {
            this.getManager().getClanById(previousTerritoryKey).ifPresent(clan -> {
                UtilEvent.dispatch(new TerritoryExitEvent(playerRef, clan));
            });
        }
    }

    private Message getTerritoryName(final Clan playerClan, final Clan territoryClan) {
        Message message = Message.raw("Wilderness").color(ChatColor.YELLOW.getColor());

        if (territoryClan != null) {
            final ClanRelation clanRelation = this.getManager().getClanRelationByClan(playerClan, territoryClan);

            message = Message.raw(territoryClan.getDisplayName()).color(clanRelation.getSuffix());
        }

        return message;
    }

    @EventHandler
    public void onPlayerDisconnect(final PlayerDisconnectEvent event) {
        this.playerLastTerritoryMap.remove(event.getPlayerRef().getUuid());
    }
}