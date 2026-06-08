package me.trae.clans.fields.loot;

import io.github.trae.di.InjectorApi;
import me.trae.clans.fields.FieldsManager;
import me.trae.core.event.BlockBreakEvent;

public interface Loot {

    default FieldsManager getFieldsManager() {
        return InjectorApi.get(FieldsManager.class);
    }

    void apply(final BlockBreakEvent blockBreakEvent);
}