package me.trae.clans.fields.storages;

import io.github.trae.database.storage.LocalStorage;
import me.trae.clans.fields.FieldsData;

import java.util.UUID;

public class FieldsDataIdStorage extends LocalStorage<UUID, FieldsData> {

    @Override
    public void index(final FieldsData fieldsData) {
        this.put(fieldsData.getId(), fieldsData);
    }

    @Override
    public void unIndex(final FieldsData fieldsData) {
        this.remove(fieldsData.getId());
    }
}