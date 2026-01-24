package me.trae.clans.clan;

import com.mongodb.client.model.Filters;
import me.trae.clans.clan.enums.ClanProperty;
import me.trae.framework.database.annotations.Repository;
import me.trae.framework.database.interfaces.IRepository;
import me.trae.framework.database.query.types.DeleteOperation;
import me.trae.framework.database.query.types.SaveOperation;
import me.trae.framework.database.query.types.UpdateOperation;
import me.trae.framework.database.types.MultiLoadRepository;
import me.trae.framework.database.types.MultiLoadResult;

import java.util.List;

@Repository(collectionName = "Clans")
public class ClanRepository implements IRepository<ClanManager, Clan, ClanProperty>, MultiLoadRepository {

    @Override
    public void saveData(final Clan clan) {
        this.getDatabaseManager().addOperation(new SaveOperation<>(this, clan));
    }

    @Override
    public void updateData(final Clan clan, final List<ClanProperty> clanPropertyList) {
        this.getDatabaseManager().addOperation(new UpdateOperation<>(this, clan, clanPropertyList, Filters.eq("_id", clan.getId())));
    }

    @Override
    public void deleteData(final Clan clan) {
        this.getDatabaseManager().addOperation(new DeleteOperation<>(this, Filters.eq("_id", clan.getId())));
    }

    @Override
    public MultiLoadResult loadData() {
        int count = 0;

        for (final Clan clan : this.getDatabaseManager().findMany(this, Filters.empty())) {
            this.getManager().addClan(clan);
            count++;
        }

        return new MultiLoadResult(count);
    }
}