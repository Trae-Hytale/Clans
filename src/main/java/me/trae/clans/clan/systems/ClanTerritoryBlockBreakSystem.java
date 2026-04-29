package me.trae.clans.clan.systems;

import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.system.CustomEntityEventSystem;
import io.github.trae.hytale.framework.system.data.SystemContext;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.InteractType;

import javax.annotation.Nullable;
import java.util.Optional;

@Component
public class ClanTerritoryBlockBreakSystem extends CustomEntityEventSystem<BreakBlockEvent> implements Module<ClansPlugin, ClanManager> {

    public ClanTerritoryBlockBreakSystem() {
        super(BreakBlockEvent.class);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }

    @Override
    public void onEvent(final BreakBlockEvent event, final SystemContext<EntityStore> systemContext) {
        if (event.isCancelled()) {
            return;
        }

        final BlockType blockType = event.getBlockType();
        if (blockType == BlockType.EMPTY) {
            return;
        }

        final Player player = systemContext.getComponent(Player.getComponentType());

        final World world = player.getWorld();
        if (world == null) {
            return;
        }

        final PlayerRef playerRef = systemContext.getComponent(PlayerRef.getComponentType());

        final Chunk chunk = Chunk.of(world, event.getTargetBlock().toVector3d());

        final Optional<Clan> territoryClanOptional = this.getManager().getClanByChunk(chunk);
        if (territoryClanOptional.isEmpty()) {
            return;
        }

        final Clan territoryClan = territoryClanOptional.get();

        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

        if (this.getManager().canInteract(playerRef, playerClanOptional.orElse(null), territoryClan, InteractType.BLOCK_INTERACT)) {
            return;
        }

        event.setCancelled(true);

        final String translationKey = Optional.ofNullable(blockType.getItem()).map(Item::getTranslationKey).orElse("unknown");

        UtilMessage.message(playerRef, "Clans", "You cannot break <green>%s</green> in %s.".formatted(Message.translation(translationKey).getAnsiMessage(), this.getManager().getClanName(this.getManager().getClanRelationByClan(playerClanOptional.orElse(null), territoryClan), territoryClan)));
    }
}