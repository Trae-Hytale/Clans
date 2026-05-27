package me.trae.clans.fields.listeners;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import me.trae.clans.ClansPlugin;
import me.trae.clans.fields.FieldsManager;
import me.trae.clans.fields.data.FieldsItem;
import me.trae.clans.fields.enums.FieldsBlockType;
import me.trae.core.client.Client;
import me.trae.core.event.BlockBreakEvent;

import java.util.concurrent.ThreadLocalRandom;

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

            final BlockLocation location = event.getLocation();

            if (!(this.getManager().isFields(location))) {
                return;
            }

            if (this.getManager().getBlockRestoreManager().getBlockRestoreByLocation(location).isPresent()) {
                return;
            }

            this.getManager().getFieldsBlockByLocation(location).ifPresent(fieldsBlock -> {
                event.setCancelled(true);

                this.getManager().getBlockRestoreManager().apply(this.getManager().createBlockRestore(fieldsBlock, fieldsBlockType));

                for (final FieldsItem fieldsItem : this.getManager().getDroppedFieldsItemList(fieldsBlockType)) {
                    if (ThreadLocalRandom.current().nextInt(0, 100) > fieldsItem.getChance()) {
                        continue;
                    }

                    final Holder<EntityStore> itemEntityStoreHolder = ItemComponent.generateItemDrop(event.getContext().getStore(), new ItemStack(fieldsItem.getId(), fieldsItem.getQuantity()), event.getLocation().getPosition3d(), Rotation3f.ZERO, 0.0F, 0.5F, 0.0F);

                    if (itemEntityStoreHolder != null) {
                        event.getContext().getCommandBuffer().addEntity(itemEntityStoreHolder, AddReason.SPAWN);
                    }
                }
            });
        });
    }
}