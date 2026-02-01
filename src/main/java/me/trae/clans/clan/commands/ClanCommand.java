package me.trae.clans.clan.commands;

import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.types.player.PlayerCommand;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Module;
import me.trae.framework.utility.UtilJava;
import me.trae.framework.utility.UtilMessage;

import java.util.LinkedHashMap;
import java.util.Optional;

@Component
public class ClanCommand extends PlayerCommand implements Module<Clans, ClanManager> {

    protected ClanCommand() {
        super("clan", "Clan management", Rank.DEFAULT);

        this.addAliases("c");
        this.addAliases("faction", "fac", "f");
    }

    @Override
    protected void onExecute(final PlayerRef player, final Client client, final String[] args, final CommandContext commandContext) {
        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(player);

        if (args.length == 1) {
            this.getManager().searchClan(player, args[0], true).ifPresent(targetClan -> this.displayClan(player, playerClanOptional.orElse(null), targetClan));
        } else {
            playerClanOptional.ifPresentOrElse(playerClan -> {
                this.displayClan(player, playerClan, playerClan);
            }, () -> {
                this.sendAbsentClanMessage(player);
            });
        }
    }

    private void displayClan(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        final ClanRelation clanRelation = this.getManager().getClanRelationByClan(playerClan, targetClan);

        UtilMessage.message(playerRef, "Clans", "%s Information:".formatted(this.getManager().getClanShortName(clanRelation, targetClan)));

        final LinkedHashMap<String, String> informationMap = UtilJava.updateMap(new LinkedHashMap<>(), map -> {
            map.put("ID", targetClan.getId().toString());
        });
    }

    public void sendAbsentClanMessage(final PlayerRef playerRef) {
        UtilMessage.message(playerRef, "Clans", "You are not in a Clan.");
    }

    public void sendPresentClanMessage(final PlayerRef playerRef) {
        UtilMessage.message(playerRef, "Clans", "You are already in a Clan.");
    }
}