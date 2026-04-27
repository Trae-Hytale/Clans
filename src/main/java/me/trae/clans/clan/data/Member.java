package me.trae.clans.clan.data;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.database.domain.data.DomainData;
import io.github.trae.database.domain.models.SubDomain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.data.interfaces.IMember;
import me.trae.clans.clan.data.properties.MemberProperty;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Member implements SubDomain<MemberProperty>, IMember {

    private final UUID id;
    private final long createdAt;

    private MemberRole role;

    public Member(final DomainData<MemberProperty> domainData) {
        this(domainData.getIdentifier(), domainData.get(Long.class, MemberProperty.CREATED_AT), MemberRole.valueOf(domainData.get(String.class, MemberProperty.ROLE)));
    }

    public Member(final PlayerRef playerRef, final MemberRole role) {
        this(playerRef.getUuid(), System.currentTimeMillis(), role);
    }

    @Override
    public Object getValueByProperty(final MemberProperty memberProperty) {
        return switch (memberProperty) {
            case CREATED_AT -> this.getCreatedAt();
            case ROLE -> this.getRole().name();
        };
    }

    @Override
    public boolean hasRole(final MemberRole role) {
        return this.getRole().ordinal() >= role.ordinal();
    }
}