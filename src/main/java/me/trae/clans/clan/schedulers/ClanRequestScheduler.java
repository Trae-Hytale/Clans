package me.trae.clans.clan.schedulers;

import io.github.trae.di.annotations.method.Scheduler;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.utilities.UtilTime;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.data.Request;
import me.trae.clans.clan.properties.ClanProperty;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class ClanRequestScheduler implements Module<ClansPlugin, ClanManager> {

    private static final long EXPIRATION = Duration.ofMinutes(5).toMillis();

    @Scheduler(period = 500, unit = TimeUnit.MILLISECONDS)
    public void onScheduler() {
        for (final Clan clan : this.getManager().getClans()) {
            for (final Request request : clan.getRequests().values()) {
                if (!(UtilTime.elapsed(request.getCreatedAt(), EXPIRATION))) {
                    continue;
                }

                clan.removeRequest(request);
                this.getManager().getRepository().update(clan, ClanProperty.REQUESTS);
            }
        }
    }
}