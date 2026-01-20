package me.trae.clans.clan.data.enums;

import lombok.Getter;
import me.trae.framework.utility.UtilString;

@Getter
public enum MemberRole {

    RECRUIT, MEMBER, ADMIN, LEADER;

    private final String name;

    MemberRole() {
        this.name = UtilString.clean(this.name());
    }
}