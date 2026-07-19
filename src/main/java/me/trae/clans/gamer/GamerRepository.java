package me.trae.clans.gamer;

import io.github.trae.database.driver.DatabaseDriver;
import io.github.trae.di.annotations.type.component.Repository;
import me.trae.clans.gamer.properties.GamerProperty;
import me.trae.core.framework.shared.gamer.AbstractGamerRepository;

@Repository
public class GamerRepository extends AbstractGamerRepository<Gamer, GamerProperty> {

    public GamerRepository(final DatabaseDriver databaseDriver) {
        super(databaseDriver, "Clans");
    }
}