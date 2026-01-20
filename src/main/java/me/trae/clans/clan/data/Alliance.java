package me.trae.clans.clan.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Alliance {

    private final UUID id;

    private boolean trusted;
}