package me.trae.clans.fields.listeners;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import me.trae.clans.ClansPlugin;
import me.trae.clans.fields.FieldsManager;
import me.trae.clans.fields.enums.FieldsBlockType;
import me.trae.core.client.Client;
import me.trae.core.event.BlockBreakEvent;

import java.util.EventListener;

@Component
public class BreakFieldsBlockListener implements Module<ClansPlugin, FieldsManager>, EventListener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(this.getManager().getFieldsConfig().isEnabled())) {
            return;
        }

        final BlockType blockType = event.getBlockType();

        if (blockType == BlockType.EMPTY) {
            return;
        }

        FieldsBlockType.getByBlockId(blockType.getId()).ifPresent(fieldsBlockType -> {
            final PlayerRef playerRef = event.getPlayerRef();

            if (this.getManager().getClientManager().getClientByPlayer(playerRef).map(Client::isAdministrating).orElse(false)) {
                return;
            }

            final BlockLocation blockLocation = BlockLocation.of(event.getWorld(), playerRef.getTransform().getPosition().toVector3i());

            if (!(this.getManager().isFields(blockLocation))) {
                return;
            }

            if (this.getManager().getBlockRestoreManager().getBlockRestoreByLocation(blockLocation).isPresent()) {
                return;
            }

            this.getManager().getFieldsBlockByLocation(blockLocation).ifPresent(fieldsBlock -> {
                event.setCancelled(true);

                this.getManager().getBlockRestoreManager().addBlockRestore(this.getManager().createBlockRestore(fieldsBlock, fieldsBlockType));

                // TODO: Drop items at Block Location from FieldsBlockType#getDroppedItemIds
            });
        });
    }
}