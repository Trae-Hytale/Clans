package me.trae.clans.clan.schedulers;

import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.utilities.UtilTime;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.data.Request;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.framework.scheduler.SubScheduler;

import java.time.Duration;
import java.util.List;

@Component
public class ClanRequestScheduler implements Module<ClansPlugin, ClanManager>, SubScheduler<Clan> {

    private static final long EXPIRATION = Duration.ofMinutes(5).toMillis();

    @Override
    public long getPeriod() {
        return 250L;
    }

    @Override
    public void onSchedule(final Clan clan) {
        for (final Request request : List.copyOf(clan.getRequests().values())) {
            if (!(UtilTime.elapsed(request.getCreatedAt(), EXPIRATION))) {
                continue;
            }

            clan.removeRequest(request);
            this.getManager().getRepository().update(clan, ClanProperty.REQUESTS);

            this.inform(clan, request);
        }
    }

    private void inform(final Clan clan, final Request request) {
        switch (request.getType()) {
            case INVITATION -> {
                this.getManager().getClientManager().getClientById(request.getTargetId()).ifPresent(targetClient -> {
                    this.getManager().messageClan(clan, "Clans", "The invitation request sent to %s has expired.".formatted(this.getManager().getPlayerName(this.getManager().getClanRelationByClan(clan, this.getManager().getClanByPlayerId(targetClient.getId()).orElse(null)), targetClient.getName())), null);

                    UtilMessage.message(targetClient.getPlayerRef(), "Clans", "The invitation request sent from %s has expired.".formatted(this.getManager().getClanFullName(this.getManager().getClanRelationByClan(this.getManager().getClanByPlayer(targetClient.getPlayerRef()).orElse(null), clan), clan)));
                });
                break;
            }
            case NEUTRAL -> {
                this.getManager().getClanById(request.getTargetId()).ifPresent(targetClan -> {
                    this.getManager().messageClan(clan, "Clans", "The neutrality request sent to %s has expired.".formatted(this.getManager().getClanFullName(this.getManager().getClanRelationByClan(clan, targetClan), targetClan)), null);

                    this.getManager().messageClan(targetClan, "Clans", "The neutrality request sent from %s has expired.".formatted(this.getManager().getClanFullName(this.getManager().getClanRelationByClan(targetClan, clan), clan)), null);
                });
                break;
            }
            case ALLIANCE -> {
                this.getManager().getClanById(request.getTargetId()).ifPresent(targetClan -> {
                    this.getManager().messageClan(clan, "Clans", "The alliance request sent to %s has expired.".formatted(this.getManager().getClanFullName(this.getManager().getClanRelationByClan(clan, targetClan), targetClan)), null);

                    this.getManager().messageClan(targetClan, "Clans", "The alliance request sent from %s has expired.".formatted(this.getManager().getClanFullName(this.getManager().getClanRelationByClan(targetClan, clan), clan)), null);
                });
                break;
            }
            case TRUST -> {
                this.getManager().getClanById(request.getTargetId()).ifPresent(targetClan -> {
                    this.getManager().messageClan(clan, "Clans", "The trust request sent to %s has expired.".formatted(this.getManager().getClanFullName(this.getManager().getClanRelationByClan(clan, targetClan), targetClan)), null);

                    this.getManager().messageClan(targetClan, "Clans", "The trust request sent from %s has expired.".formatted(this.getManager().getClanFullName(this.getManager().getClanRelationByClan(targetClan, clan), clan)), null);
                });
                break;
            }
        }
    }
}