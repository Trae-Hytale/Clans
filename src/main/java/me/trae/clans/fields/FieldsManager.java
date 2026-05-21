package me.trae.clans.fields;

import io.github.trae.di.annotations.method.ApplicationReady;
import io.github.trae.di.annotations.method.Scheduler;
import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import io.github.trae.utilities.UtilJava;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.fields.configs.FieldsConfig;
import me.trae.clans.fields.data.FieldsItem;
import me.trae.clans.fields.enums.FieldsBlockType;
import me.trae.clans.fields.interfaces.IFieldsManager;
import me.trae.clans.fields.storages.FieldsBlockIdStorage;
import me.trae.core.blockrestore.BlockRestore;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.client.ClientManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Getter
@Service
public class FieldsManager implements Manager<ClansPlugin>, IFieldsManager {

    private static final String BLOCK_RESTORE_NAME = "Fields";

    private final FieldsBlockIdStorage fieldsBlockIdStorage = new FieldsBlockIdStorage();

    private final FieldsRepository repository;

    private final FieldsConfig fieldsConfig;

    private final BlockRestoreManager blockRestoreManager;
    private final ClientManager clientManager;
    private final ClanManager clanManager;

    @ApplicationReady
    public void onApplicationReady() {
        this.flushAllFieldsBlocks();

        int count = 0;

        for (final FieldsBlock fieldsBlock : this.repository.findManySynchronously(List.of())) {
            this.addFieldsBlock(fieldsBlock);
            count++;
        }

        this.repository.setLoaded(true);

        UtilMessage.log("Database", "Loaded <yellow>%s</yellow> Fields Blocks.".formatted(count));
    }

    @Scheduler(period = 5, unit = TimeUnit.MINUTES)
    public void onScheduler() {
        if (this.getBrokenFieldsBlockList().isEmpty()) {
            return;
        }

        this.reset();

        UtilMessage.broadcast("Fields", "The <white>Fields</white> have been replenished!");
    }

    @Override
    public List<FieldsBlock> getFieldsBlockList() {
        return this.fieldsBlockIdStorage.getValues();
    }

    @Override
    public void flushAllFieldsBlocks() {
        this.fieldsBlockIdStorage.flush();
    }

    @Override
    public void addFieldsBlock(final FieldsBlock fieldsBlock) {
        this.fieldsBlockIdStorage.index(fieldsBlock);
    }

    @Override
    public void removeFieldsBlock(final FieldsBlock fieldsBlock) {
        this.fieldsBlockIdStorage.unIndex(fieldsBlock);
    }

    @Override
    public Optional<FieldsBlock> getFieldsBlockByLocation(final BlockLocation blockLocation) {
        return this.fieldsBlockIdStorage.get(FieldsBlock.ID_FORMATTER.apply(blockLocation));
    }

    @Override
    public List<FieldsBlock> getBrokenFieldsBlockList() {
        return this.getBlockRestoreManager().getBlockRestoreListByName(BLOCK_RESTORE_NAME).stream().map(blockRestore -> this.getFieldsBlockByLocation(blockRestore.getLocation())).flatMap(Optional::stream).toList();
    }

    @Override
    public List<FieldsBlock> getRemainingFieldsBlockList() {
        return this.getFieldsBlockList().stream().filter(fieldsBlock -> this.getBlockRestoreManager().getBlockRestoreByLocation(fieldsBlock.getLocation()).isEmpty()).toList();
    }

    @Override
    public boolean isFields(final BlockLocation blockLocation) {
        return this.clanManager.getClanByLocation(blockLocation).map(Clan::isFields).orElse(false);
    }

    @Override
    public BlockRestore createBlockRestore(final FieldsBlock fieldsBlock, final FieldsBlockType fieldsBlockType) {
        final String replacementBlockId = switch (fieldsBlockType) {
            case TREASURE_CHEST -> "Furniture_Ancient_Chest_Small";
            default -> "Rock_Bedrock";
        };

        return new BlockRestore(BLOCK_RESTORE_NAME, fieldsBlock.getLocation(), replacementBlockId, this.fieldsConfig.getDuration());
    }

    @Override
    public void reset() {
        this.blockRestoreManager.unApplyAll(this.blockRestoreManager.getBlockRestoreListByName(BLOCK_RESTORE_NAME));
    }

    @Override
    public List<FieldsItem> getDroppedFieldsItemList(final FieldsBlockType fieldsBlockType) {
        return switch (fieldsBlockType) {
            case COPPER_ORE -> Collections.singletonList(new FieldsItem("Ingredient_Bar_Copper", 1));
            case IRON_ORE -> Collections.singletonList(new FieldsItem("Ingredient_Bar_Iron", 1));
            case GOLD_ORE -> Collections.singletonList(new FieldsItem("Ingredient_Bar_Gold", 1));
            case THORIUM_ORE -> Collections.singletonList(new FieldsItem("Ingredient_Bar_Thorium", 1));
            case COBALT_ORE -> Collections.singletonList(new FieldsItem("Ingredient_Bar_Cobalt", 1));
            case SILVER_ORE -> Collections.singletonList(new FieldsItem("Ingredient_Bar_Silver", 1));
            case ADAMANTITE_ORE -> Collections.singletonList(new FieldsItem("Ingredient_Bar_Adamantite", 1));
            case MITHRIL_ORE -> Collections.singletonList(new FieldsItem("Ingredient_Bar_Mithril", 1));
            case TREASURE_CHEST -> UtilJava.createCollection(new ArrayList<>(), list -> {
                for (final FieldsBlockType value : FieldsBlockType.values()) {
                    if (value == FieldsBlockType.TREASURE_CHEST) {
                        continue;
                    }

                    final ThreadLocalRandom current = ThreadLocalRandom.current();

                    this.getDroppedFieldsItemList(value).forEach(fieldsItem -> list.add(new FieldsItem(fieldsItem.getId(), current.nextInt(1, 4), 90)));
                }
            });
        };
    }
}