package me.trae.clans.pillage;

import io.github.trae.di.annotations.type.component.Service;
import io.github.trae.hf.Manager;
import io.github.trae.hytale.framework.utility.UtilEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.data.Pillage;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.clans.pillage.configs.PillageConfig;
import me.trae.clans.pillage.events.PillageEndEvent;
import me.trae.clans.pillage.events.PillageStartEvent;
import me.trae.clans.pillage.interfaces.IPillageManager;

@AllArgsConstructor
@Getter
@Service
public class PillageManager implements Manager<ClansPlugin>, IPillageManager {

    private final PillageConfig pillageConfig;
    private final ClanManager clanManager;

    @Override
    public void addPillage(final Clan pillagerClan, final Clan pillageeClan) {
        final Pillage pillage = new Pillage(pillageeClan);

        pillagerClan.addPillage(pillage);
        this.clanManager.getRepository().update(pillagerClan, ClanProperty.PILLAGES);

        pillageeClan.getPillagers().add(pillagerClan.getId());
        this.clanManager.getRepository().update(pillageeClan, ClanProperty.PILLAGERS);

        UtilEvent.dispatch(new PillageStartEvent(pillagerClan, pillageeClan, pillage));
    }

    @Override
    public void removePillage(final Clan pillagerClan, final Clan pillageeClan) {
        pillagerClan.getPillageById(pillageeClan.getId()).ifPresent(pillage -> {
            pillagerClan.removePillage(pillage);
            this.clanManager.getRepository().update(pillagerClan, ClanProperty.PILLAGERS);

            pillageeClan.getPillagers().remove(pillagerClan.getId());
            this.clanManager.getRepository().update(pillageeClan, ClanProperty.PILLAGERS);

            UtilEvent.dispatch(new PillageEndEvent(pillagerClan, pillageeClan, pillage));
        });
    }
}