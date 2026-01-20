package me.trae.clans.clan.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Pillage {

    private final UUID id;
    private long systemTime;
}