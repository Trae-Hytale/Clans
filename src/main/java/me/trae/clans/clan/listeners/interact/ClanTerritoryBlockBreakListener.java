package me.trae.clans.clan.listeners.interact;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.InteractType;
import me.trae.core.event.BlockBreakEvent;

import java.util.Optional;

@Component
public class ClanTerritoryBlockBreakListener implements Module<ClansPlugin, ClanManager>, EventListener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final BlockType blockType = event.getBlockType();
        if (blockType == BlockType.EMPTY) {
            return;
        }

        final Player player = event.getPlayer();

        final Optional<PlayerRef> playerRefOptional = UtilPlayer.getPlayerRef(player);
        if (playerRefOptional.isEmpty()) {
            return;
        }

        final PlayerRef playerRef = playerRefOptional.get();

        final World world = event.getWorld();
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

        final String translationKey = Optional.ofNullable(blockType.getItem()).map(Item::getTranslationKey).orElse("unknown");

        UtilMessage.message(playerRef, "Clans", "You cannot break <green>%s</green> in %s.".formatted(Message.translation(translationKey).getAnsiMessage(), this.getManager().getClanName(this.getManager().getClanRelationByClan(playerClanOptional.orElse(null), territoryClan), territoryClan)));
    }
}