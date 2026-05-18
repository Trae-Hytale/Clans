package me.trae.clans.fields.storages;

import io.github.trae.database.storage.LocalStorage;
import me.trae.clans.fields.FieldsBlock;

import java.util.UUID;

public class FieldsBlockIdStorage extends LocalStorage<UUID, FieldsBlock> {

    @Override
    public void index(final FieldsBlock fieldsBlock) {
        this.put(fieldsBlock.getId(), fieldsBlock);
    }

    @Override
    public void unIndex(final FieldsBlock fieldsBlock) {
        this.remove(fieldsBlock.getId());
    }
}