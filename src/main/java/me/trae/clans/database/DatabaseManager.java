package me.trae.clans.database;

import me.trae.clans.Clans;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.database.AbstractDatabaseManager;
import me.trae.framework.database.interfaces.IRepository;

import java.util.List;

@Component
public class DatabaseManager extends AbstractDatabaseManager<Clans> {

    public DatabaseManager(final List<IRepository<?, ?, ?>> repositoryList) {
        super(repositoryList);
    }
}