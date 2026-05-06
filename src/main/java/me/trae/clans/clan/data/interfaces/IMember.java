package me.trae.clans.clan.data.interfaces;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import me.trae.clans.clan.data.enums.MemberRole;

public interface IMember {

    PlayerRef getPlayerRef();

    default Player getPlayer() {
        return UtilPlayer.getPlayer(this.getPlayerRef()).orElse(null);
    }

    default boolean isOnline() {
        return this.getPlayerRef() != null && this.getPlayerRef().isValid();
    }

    boolean hasRole(final MemberRole role);
}