package me.trae.clans.clan.commands.subcommands.abstracts.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.enums.MemberRole;

public interface IAbstractClanSubCommand {

    default ClanStateRequirement getRequiredState() {
        return null;
    }

    default MemberRole getRequiredMemberRole() {
        return null;
    }

    void execute(final PlayerRef playerRef, final Clan playerClan, final String[] args);
}