package me.trae.clans.fields.interfaces;

import io.github.trae.hytale.framework.wrappers.BlockLocation;
import me.trae.clans.fields.FieldsBlock;
import me.trae.clans.fields.enums.FieldsBlockType;
import me.trae.core.blockrestore.BlockRestore;

import java.util.Optional;

public interface IFieldsManager {

    void addFieldsBlock(final FieldsBlock fieldsBlock);

    void removeFieldsBlock(final FieldsBlock fieldsBlock);

    Optional<FieldsBlock> getFieldsBlockByLocation(final BlockLocation blockLocation);

    boolean isFields(final BlockLocation blockLocation);

    BlockRestore createBlockRestore(final FieldsBlock fieldsBlock, final FieldsBlockType fieldsBlockType);

    void reset();
}