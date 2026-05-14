package me.trae.clans.clan.schedulers;

import io.github.trae.di.annotations.method.Scheduler;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.utilities.UtilTime;
import lombok.AllArgsConstructor;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.core.framework.scheduler.SubScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
@Component
public class ClanScheduler implements Module<ClansPlugin, ClanManager> {

    private final ConcurrentHashMap<SubScheduler<Clan>, Long> map = new ConcurrentHashMap<>();

    private final List<SubScheduler<Clan>> clanScheduleList;

    @Scheduler(period = 50, unit = TimeUnit.MILLISECONDS)
    public void onScheduler() {
        final List<SubScheduler<Clan>> executions = new ArrayList<>();

        for (final SubScheduler<Clan> clanSchedule : this.clanScheduleList) {
            if (!(clanSchedule.shouldSchedule())) {
                continue;
            }

            final Long systemTime = this.map.get(clanSchedule);
            if (systemTime != null) {
                if (!(UtilTime.elapsed(systemTime, clanSchedule.getPeriod()))) {
                    continue;
                }

                executions.add(clanSchedule);
            } else {
                this.map.put(clanSchedule, System.currentTimeMillis());
            }
        }

        for (final Clan clan : this.getManager().getClans()) {
            executions.forEach(clanSchedule -> clanSchedule.onSchedule(clan));
        }

        executions.forEach(clanSchedule -> this.map.put(clanSchedule, System.currentTimeMillis()));
    }
}