package me.trae.clans.clan.data.enums;

import io.github.trae.utilities.UtilString;
import lombok.Getter;
import me.trae.clans.clan.data.enums.interfaces.IMemberRole;

import java.util.Optional;

@Getter
public enum MemberRole implements IMemberRole {

    RECRUIT,
    MEMBER,
    ADMIN,
    LEADER;

    private final String name;

    MemberRole() {
        this.name = UtilString.clean(this.name());
    }

    public static Optional<MemberRole> getByOrdinal(final int ordinal) {
        final MemberRole[] values = values();
        if (ordinal < 0 || ordinal >= values.length) {
            return Optional.empty();
        }

        return Optional.ofNullable(values[ordinal]);
    }

    @Override
    public boolean isHighestValue() {
        return this.ordinal() == values().length - 1;
    }

    @Override
    public boolean isLowestValue() {
        return this.ordinal() == 0;
    }

    @Override
    public String getPrefix() {
        return this.name().substring(0, 1);
    }
}