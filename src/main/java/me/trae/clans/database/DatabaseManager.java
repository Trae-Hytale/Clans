package me.trae.clans.database;

import me.trae.clans.Clans;
import me.trae.core.database.MongoManager;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.database.AbstractDatabaseManager;
import me.trae.framework.database.repository.RepositoryReference;

import java.util.List;

@Component
public class DatabaseManager extends AbstractDatabaseManager<Clans, MongoManager> {

    public DatabaseManager(final MongoManager mongoManager, final List<RepositoryReference> repositoryList) {
        super(mongoManager, repositoryList);
    }
}