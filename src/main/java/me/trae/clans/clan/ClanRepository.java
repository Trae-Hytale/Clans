package me.trae.clans.clan;

import io.github.trae.database.driver.DatabaseDriver;
import io.github.trae.database.repository.AbstractRepository;
import io.github.trae.database.repository.annotations.Repository;
import me.trae.clans.clan.properties.ClanProperty;

@Repository(databaseName = "Clans", collectionName = "Clans")
public class ClanRepository extends AbstractRepository<Clan, ClanProperty> {

    public ClanRepository(final DatabaseDriver databaseDriver) {
        super(databaseDriver);
    }

    @Override
    public void registerIndexes() {
    }
}