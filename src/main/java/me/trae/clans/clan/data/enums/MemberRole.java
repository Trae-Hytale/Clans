package me.trae.clans.clan.data.enums;

import lombok.Getter;
import me.trae.framework.utility.UtilString;
import me.trae.framework.utility.enums.ChatColor;

import java.awt.*;

@Getter
public enum MemberRole {

    RECRUIT(ChatColor.DARK_GREEN),
    MEMBER(ChatColor.AQUA),
    ADMIN(ChatColor.RED),
    LEADER(ChatColor.DARK_RED);

    private final String name;
    private final Color color;

    MemberRole(final Color color) {
        this.name = UtilString.clean(this.name());
        this.color = color;
    }

    MemberRole(final ChatColor chatColor) {
        this(chatColor.getColor());
    }
}