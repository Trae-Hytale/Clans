package me.trae.clans.clan.systems;

import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
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
public class ClanTerritoryBlockPlaceSystem extends CustomEntityEventSystem<PlaceBlockEvent> implements Module<ClansPlugin, ClanManager> {

    public ClanTerritoryBlockPlaceSystem() {
        super(PlaceBlockEvent.class);
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }

    @Override
    public void onEvent(final PlaceBlockEvent event, final SystemContext<EntityStore> systemContext) {
        if (event.isCancelled()) {
            return;
        }

        final ItemStack itemStack = event.getItemInHand();
        if (itemStack == null || itemStack == ItemStack.EMPTY) {
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

        final String translationKey = itemStack.getItem().getTranslationKey();

        UtilMessage.message(playerRef, "Clans", "You cannot place <green>%s</green> in %s.".formatted(Message.translation(translationKey).getAnsiMessage(), this.getManager().getClanName(this.getManager().getClanRelationByClan(playerClanOptional.orElse(null), territoryClan), territoryClan)));
    }
}