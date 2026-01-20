package me.trae.clans.clan.events.abstracts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.clan.Clan;
import me.trae.framework.event.custom.CustomCancellableAsyncEvent;

@AllArgsConstructor
@Getter
public class ClanCancellableAsyncEvent extends CustomCancellableAsyncEvent {

    private final Clan clan;
}