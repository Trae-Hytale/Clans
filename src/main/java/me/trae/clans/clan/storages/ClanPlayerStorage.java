package me.trae.clans.clan.storages;

import io.github.trae.database.storage.LocalStorage;
import me.trae.clans.clan.Clan;

import java.util.UUID;

public class ClanPlayerStorage extends LocalStorage<UUID, Clan> {

    @Override
    public void index(final Clan clan) {
        clan.getMembers().keySet().forEach(uuid -> this.put(uuid, clan));
    }

    @Override
    public void unIndex(final Clan clan) {
        clan.getMembers().keySet().forEach(this::remove);
    }
}