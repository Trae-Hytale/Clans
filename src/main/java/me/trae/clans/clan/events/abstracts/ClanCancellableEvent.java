package me.trae.clans.clan.events.abstracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.framework.event.custom.CustomCancellableEvent;

@AllArgsConstructor
@Getter
public class ClanCancellableEvent extends CustomCancellableEvent {

    private final Clan clan;
}