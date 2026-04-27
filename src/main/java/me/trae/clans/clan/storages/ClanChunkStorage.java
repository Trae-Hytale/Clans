package me.trae.clans.clan.storages;

import io.github.trae.database.storage.LocalStorage;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.clan.Clan;

public class ClanChunkStorage extends LocalStorage<Chunk, Clan> {

    @Override
    public void index(final Clan clan) {
        clan.getTerritory().forEach(chunk -> this.put(chunk, clan));
    }

    @Override
    public void unIndex(final Clan clan) {
        clan.getTerritory().forEach(this::remove);
    }
}