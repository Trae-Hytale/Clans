package me.trae.clans.clan.enums;

import lombok.Getter;
import me.trae.framework.utility.enums.ChatColor;

import java.awt.*;

@Getter
public enum ClanRelation {

    SELF(ChatColor.DARK_AQUA, ChatColor.AQUA),
    NEUTRAL(ChatColor.GOLD, ChatColor.YELLOW),
    TRUSTED_ALLIANCE(ChatColor.GREEN, ChatColor.DARK_GREEN),
    ALLIANCE(ChatColor.DARK_GREEN, ChatColor.GREEN),
    ENEMY(ChatColor.DARK_RED, ChatColor.RED),
    PILLAGE(ChatColor.DARK_PURPLE, ChatColor.LIGHT_PURPLE);

    private final Color prefix, suffix;

    ClanRelation(final Color prefix, final Color suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
    }

    ClanRelation(final ChatColor prefix, final ChatColor suffix) {
        this(prefix.getColor(), suffix.getColor());
    }
}