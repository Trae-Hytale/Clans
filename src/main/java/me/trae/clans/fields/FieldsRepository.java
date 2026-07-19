package me.trae.clans.fields;

import io.github.trae.database.driver.DatabaseDriver;
import io.github.trae.database.repository.AbstractRepository;
import io.github.trae.di.annotations.type.component.Repository;
import me.trae.clans.fields.properties.FieldsDataProperty;

@Repository
public class FieldsRepository extends AbstractRepository<FieldsData, FieldsDataProperty> {

    public FieldsRepository(final DatabaseDriver databaseDriver) {
        super(databaseDriver, "Clans", "Fields");
    }
}