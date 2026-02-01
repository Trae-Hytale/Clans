package me.trae.clans.clan.events.abstracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.events.abstracts.interfaces.IClanEvent;
import me.trae.framework.event.custom.CustomCancellableEvent;

@AllArgsConstructor
@Getter
public class ClanCancellableEvent extends CustomCancellableEvent implements IClanEvent {

    private final Clan clan;
}