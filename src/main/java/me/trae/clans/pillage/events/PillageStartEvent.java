package me.trae.clans.pillage.events;

import io.github.trae.hytale.framework.event.types.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.Pillage;

@AllArgsConstructor
@Getter
public class PillageStartEvent extends CustomEvent {

    private final Clan pillagerClan, pillageeClan;
    private final Pillage pillage;
}