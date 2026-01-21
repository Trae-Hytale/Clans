package me.trae.clans.clan.modules;

import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.data.Request;
import me.trae.clans.clan.enums.RelationRequestType;
import me.trae.clans.clan.wrappers.ClanUpdater;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Module;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.UtilTime;
import me.trae.framework.utility.java.PlayerTracker;

import java.time.Duration;

@Component
public class ClanRequestUpdater implements Module<Clans, ClanManager>, ClanUpdater {

    private final long RELATION_EXPIRATION = Duration.ofMinutes(5).toMillis();
    private final long INVITATION_EXPIRATION = Duration.ofMinutes(5).toMillis();

    @Override
    public void onUpdater(final Clan clan) {
        clan.getRelationRequests().values().removeIf(map -> {
            map.entrySet().removeIf(entry -> {
                final Request request = entry.getValue();

                if (UtilTime.elapsed(request.getSystemTime(), this.RELATION_EXPIRATION)) {
                    final RelationRequestType relationRequestType = entry.getKey();

                    this.getManager().getClanById(request.getId()).ifPresent(requestedClan -> {
                        this.getManager().messageClan(clan, "Clans", "The %s request sent from %s has expired.".formatted(relationRequestType.name().toLowerCase(), this.getManager().getClanFullName(this.getManager().getClanRelationByClan(clan, requestedClan), requestedClan)), null);

                        this.getManager().messageClan(requestedClan, "Clans", "The %s request your clan sent to %s has expired.".formatted(relationRequestType.name().toLowerCase(), this.getManager().getClanFullName(this.getManager().getClanRelationByClan(requestedClan, clan), clan)), null);
                    });

                    return true;
                }

                return false;
            });

            return map.isEmpty();
        });

        clan.getInvitationRequests().values().removeIf(request -> {
            if (UtilTime.elapsed(request.getSystemTime(), this.INVITATION_EXPIRATION)) {
                PlayerTracker.getPlayer(request.getId()).ifPresent(player -> {
                    UtilMessage.message(player, "Clans", "The invitation request from %s has expired.".formatted(this.getManager().getClanFullName(this.getManager().getClanRelationByClan(this.getManager().getClanByPlayer(player).orElse(null), clan), clan)));

                    this.getManager().messageClan(clan, "Clans", "The invitation request your clan sent to <yellow>%s</yellow> has expired.".formatted(player.getUsername()), null);
                });

                return true;
            }

            return false;
        });
    }
}