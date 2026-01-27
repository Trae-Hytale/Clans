package me.trae.clans.clan;

import me.trae.clans.clan.enums.ClanProperty;
import me.trae.framework.database.annotations.Repository;
import me.trae.framework.database.domain.data.DomainData;
import me.trae.framework.database.query.types.DeleteOperation;
import me.trae.framework.database.query.types.SaveOperation;
import me.trae.framework.database.query.types.UpdateOperation;
import me.trae.framework.database.repository.IRepository;
import me.trae.framework.database.types.MultiLoadRepository;
import me.trae.framework.database.types.MultiLoadResult;
import me.trae.framework.utility.MongoShared;
import me.trae.framework.utility.UtilMessage;

import java.util.List;

@Repository(collectionName = "Clans")
public class ClanRepository implements IRepository<ClanManager, Clan, ClanProperty>, MultiLoadRepository {

    @Override
    public void saveData(final Clan clan) {
        this.getDatabaseManager().addOperation(new SaveOperation<>(this, clan));
    }

    @Override
    public void updateData(final Clan clan, final List<ClanProperty> clanPropertyList) {
        this.getDatabaseManager().addOperation(new UpdateOperation<>(this, clan, clanPropertyList));
    }

    @Override
    public void deleteData(final Clan clan) {
        this.getDatabaseManager().addOperation(new DeleteOperation<>(this, clan));
    }

    @Override
    public MultiLoadResult loadData() {
        int count = 0;

        for (final DomainData<ClanProperty> data : this.getDatabaseManager().findManySynchronously(this, MongoShared.emptyFilter())) {
            try {
                final Clan clan = new Clan(data);

                this.getManager().addClan(clan);

                count++;
            } catch (final Exception e) {
                UtilMessage.log(this.getRepositoryName(), "Failed to load clan: <red>%s</red>".formatted(data.getId()));
                e.printStackTrace();
            }
        }

        return new MultiLoadResult(count);
    }
}