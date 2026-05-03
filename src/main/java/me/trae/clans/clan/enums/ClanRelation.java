package me.trae.clans.clan.enums;

import io.github.trae.hytale.framework.utility.enums.ChatColor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.awt.*;

@AllArgsConstructor
@Getter
public enum ClanRelation {

    NEUTRAL(ChatColor.GOLD.getColor(), ChatColor.YELLOW.getColor()),
    SELF(ChatColor.DARK_AQUA.getColor(), ChatColor.AQUA.getColor()),
    ALLIANCE(ChatColor.DARK_GREEN.getColor(), ChatColor.GREEN.getColor()),
    TRUSTED_ALLIANCE(ChatColor.GREEN.getColor(), ChatColor.DARK_GREEN.getColor()),
    ENEMY(ChatColor.DARK_RED.getColor(), ChatColor.RED.getColor()),
    PILLAGE(ChatColor.DARK_PURPLE.getColor(), ChatColor.LIGHT_PURPLE.getColor());

    private final Color prefix, suffix;

    public static boolean isTeammate(final ClanRelation clanRelation) {
        return switch (clanRelation) {
            case SELF, ALLIANCE, TRUSTED_ALLIANCE -> true;
            default -> false;
        };
    }
}