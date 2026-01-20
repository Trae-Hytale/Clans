package me.trae.clans.clan;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AdminClan extends Clan {

    private boolean safe;

    public AdminClan(final UUID id) {
        super(id);
    }
}