package me.trae.clans.fields.loot.impl.item;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.trae.utilities.UtilNumber;
import lombok.Getter;
import me.trae.clans.fields.loot.impl.ChanceLoot;
import me.trae.clans.fields.loot.impl.item.interfaces.IItemLoot;
import me.trae.core.event.BlockBreakEvent;

@Getter
public abstract class ItemLoot extends ChanceLoot implements IItemLoot {

    private final String itemId;

    public ItemLoot(final int chance, final String itemId) {
        super(chance);

        this.itemId = itemId;
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemStack(this.getItemId(), this.getQuantity());
    }

    @Override
    public void apply(final BlockBreakEvent blockBreakEvent) {
        if (this.getChance() != 100 && UtilNumber.getRandomNumber(Integer.class, 1, 100) > this.getChance()) {
            return;
        }

        if (this.getFieldsManager().getFieldsConfig().isInsertLootIntoInventory()) {
            this.insertItem(blockBreakEvent, this.getItemStack());
        } else {
            this.dropItem(blockBreakEvent, this.getItemStack());
        }
    }

    private void insertItem(final BlockBreakEvent event, final ItemStack itemStack) {
        final Ref<EntityStore> playerReference = event.getPlayerRef().getReference();

        if (playerReference != null) {
            final ItemStackTransaction itemStackTransaction = Player.giveItem(itemStack, playerReference, event.getContext().getStore());

            if (!(itemStackTransaction.succeeded())) {
                this.dropItem(event, itemStack);
            } else if (itemStackTransaction.getRemainder() != null) {
                this.dropItem(event, itemStackTransaction.getRemainder());
            }

            return;
        }

        this.dropItem(event, itemStack);
    }

    private void dropItem(final BlockBreakEvent event, final ItemStack itemStack) {
        final Holder<EntityStore> itemEntityStoreHolder = ItemComponent.generateItemDrop(event.getContext().getStore(), itemStack, event.getLocation().getPosition3d(), Rotation3f.ZERO, 0.0F, 0.5F, 0.0F);

        if (itemEntityStoreHolder != null) {
            event.getContext().getCommandBuffer().addEntity(itemEntityStoreHolder, AddReason.SPAWN);
        }
    }
}