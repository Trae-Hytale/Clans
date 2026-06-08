package me.trae.clans.fields.interfaces;

import io.github.trae.hytale.framework.wrappers.BlockLocation;
import me.trae.clans.fields.FieldsData;
import me.trae.clans.fields.blocks.FieldsBlock;

import java.util.List;
import java.util.Optional;

public interface IFieldsManager {

    void flushData();

    List<FieldsData> getData();

    void addData(final FieldsData fieldsData);

    void removeData(final FieldsData fieldsData);

    Optional<FieldsData> getDataByLocation(final BlockLocation location);

    boolean isFieldsByLocation(final BlockLocation location);

    Optional<FieldsBlock> getBlockById(final String id);

    List<FieldsData> getBrokenDataList();

    List<FieldsData> getRemainingDataList();

    void replenish();

    long getDuration();
}