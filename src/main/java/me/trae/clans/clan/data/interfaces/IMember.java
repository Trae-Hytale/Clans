package me.trae.clans.clan.data.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.clan.data.enums.MemberRole;

public interface IMember {

    PlayerRef getPlayerRef();

    default boolean isOnline() {
        return this.getPlayerRef() != null && this.getPlayerRef().isValid();
    }

    boolean hasRole(final MemberRole role);
}