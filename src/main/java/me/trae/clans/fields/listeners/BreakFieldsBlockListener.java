package me.trae.clans.fields.listeners;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import me.trae.clans.ClansPlugin;
import me.trae.clans.fields.FieldsManager;
import me.trae.clans.fields.blockrestore.FieldsBlockRestore;
import me.trae.clans.fields.events.BreakFieldsBlockEvent;
import me.trae.clans.fields.loot.Loot;
import me.trae.core.client.Client;
import me.trae.core.event.BlockBreakEvent;

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

        this.getManager().getBlockById(blockType.getId()).ifPresent(fieldsBlock -> {
            final PlayerRef playerRef = event.getPlayerRef();

            if (this.getManager().getClientManager().getClientByPlayer(playerRef).map(Client::isAdministrating).orElse(false)) {
                return;
            }

            final BlockLocation location = event.getLocation();

            if (!(this.getManager().isFieldsByLocation(location))) {
                return;
            }

            if (this.getManager().getBlockRestoreManager().getBlockRestoreByLocation(location).isPresent()) {
                return;
            }

            this.getManager().getDataByLocation(location).ifPresent(fieldsData -> {
                event.setCancelled(true);

                if (UtilEvent.supply(new BreakFieldsBlockEvent(fieldsData, fieldsBlock, playerRef)).isCancelled()) {
                    return;
                }

                this.getManager().getBlockRestoreManager().apply(new FieldsBlockRestore(fieldsData, fieldsBlock, this.getManager().getDuration()));

                for (final Loot loot : fieldsBlock.getLootList()) {
                    loot.apply(event);
                }
            });
        });
    }
}