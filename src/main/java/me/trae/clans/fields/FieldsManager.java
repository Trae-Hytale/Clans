package me.trae.clans.fields;

import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.fields.configs.FieldsConfig;
import me.trae.clans.fields.enums.FieldsBlockType;
import me.trae.clans.fields.interfaces.IFieldsManager;
import me.trae.clans.fields.storages.FieldsBlockIdStorage;
import me.trae.core.blockrestore.BlockRestore;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.client.ClientManager;

import java.util.Optional;

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
    public boolean isFields(final BlockLocation blockLocation) {
        return this.clanManager.getClanByLocation(blockLocation).map(Clan::isFields).orElse(false);
    }

    @Override
    public BlockRestore createBlockRestore(final FieldsBlock fieldsBlock, final FieldsBlockType fieldsBlockType) {
        final String replacementBlockId = switch (fieldsBlockType) {
            case TREASURE_CHEST -> "Air";
            default -> "Rock_Bedrock";
        };

        return new BlockRestore(BLOCK_RESTORE_NAME, fieldsBlock.getLocation(), replacementBlockId, this.fieldsConfig.getDuration());
    }

    @Override
    public void reset() {
        this.blockRestoreManager.unApplyAll(this.blockRestoreManager.getBlockRestoreListByName(BLOCK_RESTORE_NAME));
    }
}