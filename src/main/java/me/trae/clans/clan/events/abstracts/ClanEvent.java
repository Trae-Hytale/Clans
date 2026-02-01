package me.trae.clans.clan.events.abstracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.interfaces.IClanEvent;
import me.trae.framework.event.custom.CustomEvent;

@AllArgsConstructor
@Getter
public class ClanEvent extends CustomEvent implements IClanEvent {

    private final Clan clan;
}