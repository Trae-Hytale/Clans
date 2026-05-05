package me.trae.clans.clan.commands.subcommands.admin;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;

@Component
public class SafeCommand extends AbstractClanSubCommand {

    public SafeCommand() {
        super("safe", "Toggle Safe Clan", Rank.OWNER);
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_PRESENT;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (!(client.isAdministrating())) {
            UtilMessage.message(playerRef, "Clans", "You must be administrating to toggle Safe Mode!");
            return;
        }

        if (!(playerClan.isAdmin())) {
            UtilMessage.message(playerRef, "Clans", "%s is not an Admin Clan!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.SELF, playerClan)));
            return;
        }

        if (playerClan.isSafe()) {
            playerClan.setSafe(false);

            UtilMessage.message(playerRef, "Clans", "%s is no longer a Safe Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.SELF, playerClan)));
        } else {
            playerClan.setSafe(true);

            UtilMessage.message(playerRef, "Clans", "%s is now a Safe Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.SELF, playerClan)));
        }

        this.getModule().getManager().getRepository().update(playerClan, ClanProperty.SAFE);
    }
}