package me.trae.clans.clan.teleport;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.wrappers.Location;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.core.teleport.TeleportData;

@Getter
public class ClanHomeTeleportData extends TeleportData {

    private final Clan clan;

    public ClanHomeTeleportData(final Clan clan, final PlayerRef playerRef, final Player player, final Location location, final long duration) {
        super(playerRef, player, location, duration);

        this.setCancelOnMovement(true);
        this.setCancelOnDamage(true);

        this.clan = clan;
    }
}