package me.trae.clans.clan.storages;

import io.github.trae.database.storage.LocalStorage;
import me.trae.clans.clan.Clan;

import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

public class ClanNameStorage extends LocalStorage<String, Clan> {

    @Override
    public void put(final String name, final Clan clan, final Duration ttl) {
        super.put(name.toLowerCase(Locale.ROOT), clan, ttl);
    }

    @Override
    public void remove(final String name) {
        super.remove(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public Optional<Clan> get(final String name) {
        return super.get(name.toLowerCase(Locale.ROOT));
    }

    @Override
    public void index(final Clan clan) {
        this.put(clan.getName(), clan);
    }

    @Override
    public void unIndex(final Clan clan) {
        this.remove(clan.getName());
    }
}