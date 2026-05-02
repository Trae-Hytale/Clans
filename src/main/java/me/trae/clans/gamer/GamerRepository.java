package me.trae.clans.gamer;

import io.github.trae.database.driver.DatabaseDriver;
import io.github.trae.database.repository.annotations.Repository;
import me.trae.clans.gamer.properties.GamerProperty;
import me.trae.core.framework.gamer.AbstractGamerRepository;

@Repository(databaseName = "Clans", collectionName = "Gamers")
public class GamerRepository extends AbstractGamerRepository<Gamer, GamerProperty> {

    public GamerRepository(final DatabaseDriver databaseDriver) {
        super(databaseDriver);
    }

    @Override
    public void registerIndexes() {
    }
}