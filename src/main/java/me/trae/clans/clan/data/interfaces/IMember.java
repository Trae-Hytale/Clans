package me.trae.clans.clan.data.interfaces;

import me.trae.clans.clan.data.enums.MemberRole;

public interface IMember {

    boolean hasRole(final MemberRole role);
}