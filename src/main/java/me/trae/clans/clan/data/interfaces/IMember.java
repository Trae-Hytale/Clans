package me.trae.clans.clan.data.interfaces;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.clan.data.enums.MemberRole;

public interface IMember {

    boolean hasRole(final MemberRole role);

    PlayerRef getPlayerRef();

    Player getPlayer();

    boolean isOnline();
}