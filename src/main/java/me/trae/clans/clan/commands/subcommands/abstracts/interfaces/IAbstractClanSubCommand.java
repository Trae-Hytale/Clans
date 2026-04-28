package me.trae.clans.clan.commands.subcommands.abstracts.interfaces;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.core.client.Client;

public interface IAbstractClanSubCommand {

    default ClanStateRequirement getRequiredState() {
        return null;
    }

    default MemberRole getRequiredMemberRole() {
        return null;
    }

    default boolean isRequiredMemberRoleCheckOnExecute() {
        return true;
    }

    void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args);
}