package me.trae.clans.clan.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Enemy {

    private final UUID id;

    private int points;
}