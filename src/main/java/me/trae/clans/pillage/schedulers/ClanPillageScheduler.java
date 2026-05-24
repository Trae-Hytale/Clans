package me.trae.clans.pillage.schedulers;

import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.utilities.UtilTime;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.data.Pillage;
import me.trae.clans.pillage.PillageManager;
import me.trae.core.scheduler.annotations.SubScheduler;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ClanPillageScheduler implements Module<ClansPlugin, PillageManager> {

    @SubScheduler(period = 250, unit = TimeUnit.MILLISECONDS)
    public void onSubScheduler(final Clan clan) {
        for (final Pillage pillage : List.copyOf(clan.getPillages().values())) {
            if (!(UtilTime.elapsed(pillage.getCreatedAt(), this.getManager().getPillageConfig().getDuration()))) {
                continue;
            }

            this.getManager().getClanManager().getClanById(pillage.getId()).ifPresent(pillageeClan -> {
                this.getManager().removePillage(clan, pillageeClan);
            });
        }
    }
}