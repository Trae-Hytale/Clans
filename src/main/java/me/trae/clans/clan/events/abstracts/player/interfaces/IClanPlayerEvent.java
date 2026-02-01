package me.trae.clans.clan.events.abstracts.player.interfaces;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.clan.events.abstracts.interfaces.IClanEvent;
import me.trae.core.client.Client;

public interface IClanPlayerEvent extends IClanEvent {

    PlayerRef getPlayer();

    Client getPlayerClient();
}