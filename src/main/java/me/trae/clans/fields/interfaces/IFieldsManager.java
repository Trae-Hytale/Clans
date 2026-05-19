package me.trae.clans.fields.interfaces;

import io.github.trae.hytale.framework.wrappers.BlockLocation;
import me.trae.clans.fields.FieldsBlock;
import me.trae.clans.fields.data.FieldsItem;
import me.trae.clans.fields.enums.FieldsBlockType;
import me.trae.core.blockrestore.BlockRestore;

import java.util.List;
import java.util.Optional;

public interface IFieldsManager {

    List<FieldsBlock> getFieldsBlockList();

    void flushAllFieldsBlocks();

    void addFieldsBlock(final FieldsBlock fieldsBlock);

    void removeFieldsBlock(final FieldsBlock fieldsBlock);

    Optional<FieldsBlock> getFieldsBlockByLocation(final BlockLocation blockLocation);

    List<FieldsBlock> getBrokenFieldsBlockList();

    List<FieldsBlock> getRemainingFieldsBlockList();

    boolean isFields(final BlockLocation blockLocation);

    BlockRestore createBlockRestore(final FieldsBlock fieldsBlock, final FieldsBlockType fieldsBlockType);

    void reset();

    List<FieldsItem> getDroppedFieldsItemList(final FieldsBlockType fieldsBlockType);
}