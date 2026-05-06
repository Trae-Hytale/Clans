package me.trae.clans.clan.listeners.interact;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.PlaceBlockEvent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.system.SystemListener;
import io.github.trae.hytale.framework.system.annotations.EventSystemHandler;
import io.github.trae.hytale.framework.system.data.EventSystemContext;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.InteractType;

import java.util.Optional;

@Component
public class ClanTerritoryBlockPlaceListener implements Module<ClansPlugin, ClanManager>, SystemListener {

    @EventSystemHandler(query = Player.class)
    public void onPlaceBlock(final EventSystemContext<EntityStore, PlaceBlockEvent> context) {
        final PlaceBlockEvent event = context.getEvent();

        if (event.isCancelled()) {
            return;
        }

        final ItemStack itemStack = event.getItemInHand();
        if (itemStack == null || itemStack == ItemStack.EMPTY) {
            return;
        }

        final Player player = context.getComponent(Player.getComponentType());

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

        if (this.getManager().canInteract(playerRef, playerClanOptional.orElse(null), territoryClan, InteractType.BLOCK_INTERACT)) {
            return;
        }

        event.setCancelled(true);

        final String translationKey = itemStack.getItem().getTranslationKey();

        UtilMessage.message(playerRef, "Clans", "You cannot place <green>%s</green> in %s.".formatted(Message.translation(translationKey).getAnsiMessage(), this.getManager().getClanName(this.getManager().getClanRelationByClan(playerClanOptional.orElse(null), territoryClan), territoryClan)));
    }
}