package me.trae.clans.fields.loot.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.fields.loot.Loot;

@AllArgsConstructor
@Getter
public abstract class ChanceLoot implements Loot {

    private final int chance;
}