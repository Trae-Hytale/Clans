package me.trae.clans.gamer.events;

import io.github.trae.hytale.framework.event.types.CustomEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.gamer.Gamer;

@AllArgsConstructor
@Getter
public class GamerCoinsUpdateEvent extends CustomEvent {

    private final Gamer gamer;
    private final int amount;
}