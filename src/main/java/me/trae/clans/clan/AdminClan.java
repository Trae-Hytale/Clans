package me.trae.clans.clan;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.enums.ClanProperty;
import me.trae.framework.database.domain.data.DomainData;

import java.util.UUID;

@Getter
@Setter
public class AdminClan extends Clan {

    private boolean safe;

    public AdminClan(final UUID id) {
        super(id);
    }

    public AdminClan(final PlayerRef playerRef, final String name) {
        super(playerRef, name);
    }

    public AdminClan(final DomainData<ClanProperty> data) {
        super(data);

        this.safe = data.get(Boolean.class, ClanProperty.SAFE);
    }
}