package me.trae.clans.clan.storages;

import io.github.trae.database.storage.LocalStorage;
import me.trae.clans.clan.Clan;

import java.util.UUID;

public class ClanIdStorage extends LocalStorage<UUID, Clan> {

    @Override
    public void index(final Clan clan) {
        this.put(clan.getId(), clan);
    }

    @Override
    public void unIndex(final Clan clan) {
        this.remove(clan.getId());
    }
}