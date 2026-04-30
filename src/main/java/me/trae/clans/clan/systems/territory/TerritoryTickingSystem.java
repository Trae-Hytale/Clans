package me.trae.clans.clan.systems.territory;

import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.system.CustomEntityTickingSystem;
import io.github.trae.hytale.framework.system.data.SystemContext;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TerritoryTickingSystem extends CustomEntityTickingSystem implements Module<ClansPlugin, ClanManager> {

    private final ConcurrentHashMap<UUID, String> playerLastTerritoryMap = new ConcurrentHashMap<>();

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

        final Message territoryName = this.getTerritoryName(playerClanOptional.orElse(null), territoryClanOptional.orElse(null));

        final String currentTerritoryKey = territoryClanOptional.map(clan -> clan.getId().toString()).orElse("NULL");
        final String previousTerritoryKey = this.playerLastTerritoryMap.get(playerRef.getUuid());

        if (currentTerritoryKey.equals(previousTerritoryKey)) {
            return;
        }

        this.playerLastTerritoryMap.put(playerRef.getUuid(), currentTerritoryKey);

        EventTitleUtil.hideEventTitleFromPlayer(playerRef, 0.0F);
        EventTitleUtil.showEventTitleToPlayer(playerRef, territoryName, Message.raw("Territory").color(ChatColor.RED.getColor()).bold(true), false);

        UtilMessage.message(playerRef, "Territory", territoryName);
    }

    private Message getTerritoryName(final Clan playerClan, final Clan territoryClan) {
        Message message = Message.raw("Wilderness").color(ChatColor.YELLOW.getColor());

        if (territoryClan != null) {
            final ClanRelation clanRelation = this.getManager().getClanRelationByClan(playerClan, territoryClan);

            message = Message.raw(territoryClan.getDisplayName()).color(clanRelation.getSuffix());
        }

        return message;
    }
}