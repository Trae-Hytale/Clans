package me.trae.clans.pillage.interfaces;

import me.trae.clans.clan.Clan;

public interface IPillageManager {

    void addPillage(final Clan pillagerClan, final Clan pillageeClan);

    void removePillage(final Clan pillagerClan, final Clan pillageeClan);
}