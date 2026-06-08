package me.trae.clans.fields;

import com.hypixel.hytale.server.core.universe.Universe;
import io.github.trae.di.annotations.method.ApplicationReady;
import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.fields.blockrestore.FieldsBlockRestore;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.clans.fields.configs.FieldsConfig;
import me.trae.clans.fields.interfaces.IFieldsManager;
import me.trae.clans.fields.storages.FieldsDataIdStorage;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.client.ClientManager;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Service
public class FieldsManager implements Manager<ClansPlugin>, IFieldsManager {

    private final FieldsDataIdStorage fieldsDataIdStorage = new FieldsDataIdStorage();

    private final FieldsRepository repository;

    private final FieldsConfig fieldsConfig;

    private final List<FieldsBlock> fieldsBlockList;

    private final BlockRestoreManager blockRestoreManager;
    private final ClanManager clanManager;
    private final ClientManager clientManager;

    @ApplicationReady
    public void onApplicationReady() {
        this.flushData();

        int count = 0;

        for (final FieldsData fieldsData : this.repository.findManySynchronously(List.of())) {
            this.addData(fieldsData);
            count++;
        }

        this.repository.setLoaded(true);

        UtilMessage.log("Database", "Loaded <yellow>%s</yellow> Fields Blocks.".formatted(count));
    }

    @Override
    public void flushData() {
        this.fieldsDataIdStorage.flush();
    }

    @Override
    public List<FieldsData> getData() {
        return this.fieldsDataIdStorage.getValues();
    }

    @Override
    public void addData(final FieldsData fieldsData) {
        this.fieldsDataIdStorage.index(fieldsData);
    }

    @Override
    public void removeData(final FieldsData fieldsData) {
        this.fieldsDataIdStorage.unIndex(fieldsData);
    }

    @Override
    public Optional<FieldsData> getDataByLocation(final BlockLocation location) {
        return this.fieldsDataIdStorage.get(FieldsData.ID_FORMATTER.apply(location));
    }

    @Override
    public boolean isFieldsByLocation(final BlockLocation location) {
        return this.clanManager.getClanByLocation(location).map(Clan::isFields).orElse(false);
    }

    @Override
    public Optional<FieldsBlock> getBlockById(final String id) {
        return this.fieldsBlockList.stream().filter(value -> value.getBlockIds().contains(id)).findFirst();
    }

    @Override
    public List<FieldsData> getBrokenDataList() {
        return this.getData().stream().filter(value -> this.blockRestoreManager.getBlockRestoreByLocation(value.getLocation()).isPresent()).toList();
    }

    @Override
    public List<FieldsData> getRemainingDataList() {
        return this.getData().stream().filter(value -> this.blockRestoreManager.getBlockRestoreByLocation(value.getLocation()).isEmpty()).toList();
    }

    @Override
    public void replenish() {
        this.blockRestoreManager.unApplyAll(this.blockRestoreManager.getBlockRestoreListByName(FieldsBlockRestore.NAME));
    }

    @Override
    public long getDuration() {
        final int playerCount = Universe.get().getPlayerCount();

        long duration = this.fieldsConfig.getStaticDuration();

        if (duration <= 0L) {
            for (final Map.Entry<Integer, Long> entry : this.fieldsConfig.getDynamicDurations().entrySet()) {
                if (playerCount < entry.getKey()) {
                    duration = entry.getValue();
                    break;
                }
            }
        }

        return duration;
    }
}