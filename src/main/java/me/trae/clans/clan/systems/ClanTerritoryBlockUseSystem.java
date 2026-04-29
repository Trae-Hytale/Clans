package me.trae.clans.clan.systems;

import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.UseBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.system.CustomEntityEventSystem;
import io.github.trae.hytale.framework.system.data.SystemContext;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.InteractType;
import me.trae.core.utility.UtilBlock;

import javax.annotation.Nullable;
import java.util.Optional;

@Component
public class ClanTerritoryBlockUseSystem extends CustomEntityEventSystem<UseBlockEvent.Pre> implements Module<ClansPlugin, ClanManager> {

    public ClanTerritoryBlockUseSystem() {
        super(UseBlockEvent.Pre.class);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Player.getComponentType();
    }

    @Override
    public void onEvent(final UseBlockEvent.Pre event, final SystemContext<EntityStore> systemContext) {
        if (event.isCancelled()) {
            return;
        }

        final BlockType blockType = event.getBlockType();
        if (blockType == BlockType.EMPTY) {
            return;
        }

        final Player player = systemContext.getComponent(Player.getComponentType());

        final Optional<PlayerRef> playerRefOptional = UtilPlayer.getPlayerRef(player);
        if (playerRefOptional.isEmpty()) {
            return;
        }

        final PlayerRef playerRef = playerRefOptional.get();

        final World world = player.getWorld();
        if (world == null) {
            return;
        }

        final Chunk chunk = Chunk.of(world, event.getTargetBlock().toVector3d());

        final Optional<Clan> territoryClanOptional = this.getManager().getClanByChunk(chunk);
        if (territoryClanOptional.isEmpty()) {
            return;
        }

        final Clan territoryClan = territoryClanOptional.get();

        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

        if (this.canInteract(playerRef, playerClanOptional.orElse(null), territoryClan, blockType)) {
            return;
        }

        event.setCancelled(true);

        final String translationKey = Optional.ofNullable(blockType.getItem()).map(Item::getTranslationKey).orElse("unknown");

        UtilMessage.message(playerRef, "Clans", "You cannot use <green>%s</green> in %s.".formatted(Message.translation(translationKey).getAnsiMessage(), this.getManager().getClanName(this.getManager().getClanRelationByClan(playerClanOptional.orElse(null), territoryClan), territoryClan)));
    }

    private boolean canInteract(final PlayerRef playerRef, final Clan playerClan, final Clan territoryClan, final BlockType blockType) {
        if (UtilBlock.isContainer(blockType)) {
            return this.getManager().canInteract(playerRef, playerClan, territoryClan, InteractType.CONTAINER_INTERACT);
        }

        if (UtilBlock.isGateway(blockType)) {
            return this.getManager().canInteract(playerRef, playerClan, territoryClan, InteractType.GATEWAY_INTERACT);
        }

        return false;
    }
}