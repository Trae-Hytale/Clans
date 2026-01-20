package me.trae.clans.clan.data;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.data.interfaces.IMember;
import me.trae.framework.utility.java.PlayerTracker;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Member implements IMember {

    private final UUID id;

    private MemberRole role;

    @Override
    public boolean hasRole(final MemberRole role) {
        return this.getRole().ordinal() >= role.ordinal();
    }

    @Override
    public PlayerRef getPlayer() {
        return PlayerTracker.getOnlinePlayer(this.getId());
    }

    @Override
    public boolean isOnline() {
        return PlayerTracker.isOnlinePlayer(this.getId());
    }
}