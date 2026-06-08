package me.trae.clans.fields.listeners;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import me.trae.clans.ClansPlugin;
import me.trae.clans.fields.FieldsData;
import me.trae.clans.fields.FieldsManager;
import me.trae.core.client.Client;
import me.trae.core.event.BlockBreakEvent;
import me.trae.core.event.BlockPlaceEvent;

@Component
public class ManageFieldsBlockListener implements Module<ClansPlugin, FieldsManager>, EventListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final ItemStack itemInHand = event.getItemInHand();

        if (itemInHand == null || itemInHand == ItemStack.EMPTY) {
            return;
        }

        if (this.getManager().getBlockById(itemInHand.getItemId()).isEmpty()) {
            return;
        }

        final PlayerRef playerRef = event.getPlayerRef();

        if (!(this.getManager().getClientManager().getClientByPlayer(playerRef).map(Client::isAdministrating).orElse(false))) {
            return;
        }

        final BlockLocation location = event.getLocation();

        if (!(this.getManager().isFieldsByLocation(location))) {
            return;
        }

        if (this.getManager().getDataByLocation(location).isPresent()) {
            return;
        }

        final FieldsData fieldsData = new FieldsData(location, itemInHand.getItemId());

        this.getManager().addData(fieldsData);
        this.getManager().getRepository().save(fieldsData);

        UtilMessage.message(playerRef, "Fields", "Saved <green>%s</green> at (<yellow>%s</yellow>, <yellow>%s</yellow>, <yellow>%s</yellow>).".formatted(itemInHand.getItemId(), location.getX(), location.getY(), location.getZ()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final BlockType blockType = event.getBlockType();

        if (blockType == BlockType.EMPTY) {
            return;
        }

        if (this.getManager().getBlockById(blockType.getId()).isEmpty()) {
            return;
        }

        final PlayerRef playerRef = event.getPlayerRef();

        if (!(this.getManager().getClientManager().getClientByPlayer(playerRef).map(Client::isAdministrating).orElse(false))) {
            return;
        }

        final BlockLocation location = event.getLocation();

        if (!(this.getManager().isFieldsByLocation(location))) {
            return;
        }

        this.getManager().getDataByLocation(location).ifPresent(fieldsData -> {
            this.getManager().removeData(fieldsData);
            this.getManager().getRepository().delete(fieldsData);

            UtilMessage.message(playerRef, "Fields", "Deleted <red>%s</red> at (<yellow>%s</yellow>, <yellow>%s</yellow>, <yellow>%s</yellow>).".formatted(blockType.getId(), location.getX(), location.getY(), location.getZ()));
        });
    }
}