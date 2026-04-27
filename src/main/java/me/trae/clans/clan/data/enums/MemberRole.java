package me.trae.clans.clan.data.enums;

import io.github.trae.utilities.UtilString;
import lombok.Getter;
import me.trae.clans.clan.data.enums.interfaces.IMemberRole;

@Getter
public enum MemberRole implements IMemberRole {

    RECRUIT, MEMBER, ADMIN, LEADER;

    private final String name;

    MemberRole() {
        this.name = UtilString.clean(this.name());
    }

    @Override
    public String getPrefix() {
        return this.name().substring(0, 1);
    }
}