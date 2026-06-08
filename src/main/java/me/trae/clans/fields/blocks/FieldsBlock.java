package me.trae.clans.fields.blocks;

import me.trae.clans.fields.loot.Loot;

import java.util.List;

public interface FieldsBlock {

    List<String> getBlockIds();

    List<Loot> getLootList();

    String getReplacementBlockId();
}