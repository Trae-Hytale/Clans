package me.trae.clans.clan.teleport;

import com.hypixel.hytale.server.core.entity.entities.Player;
import io.github.trae.hytale.framework.wrappers.Location;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.core.teleport.TeleportData;

import java.util.function.Consumer;

@Getter
public class ClanHomeTeleportData extends TeleportData {

    private final Clan clan;

    public ClanHomeTeleportData(final Clan clan, final Player player, final Location location, final long duration, final Consumer<TeleportData> preConsumer, final Consumer<TeleportData> postConsumer) {
        super(player, location, System.currentTimeMillis(), duration, preConsumer, postConsumer);

        this.clan = clan;
    }
}