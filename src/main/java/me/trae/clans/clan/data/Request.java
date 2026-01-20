package me.trae.clans.clan.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class Request {

    private final UUID id;
    private final long systemTime;
}