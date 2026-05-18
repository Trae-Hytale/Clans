package me.trae.clans.fields;

import io.github.trae.database.driver.DatabaseDriver;
import io.github.trae.database.repository.AbstractRepository;
import io.github.trae.database.repository.annotations.Repository;
import me.trae.clans.fields.properties.FieldsBlockProperty;

@Repository(databaseName = "Clans", collectionName = "FieldsBlocks")
public class FieldsRepository extends AbstractRepository<FieldsBlock, FieldsBlockProperty> {

    public FieldsRepository(final DatabaseDriver databaseDriver) {
        super(databaseDriver);
    }

    @Override
    public void registerIndexes() {
    }
}