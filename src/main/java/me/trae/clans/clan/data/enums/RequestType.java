package me.trae.clans.clan.data.enums;

import io.github.trae.utilities.UtilString;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Optional;

public enum RequestType {

    INVITATION, NEUTRAL, ALLIANCE, TRUST;

    private static final LinkedHashMap<String, RequestType> BY_NAME_MAP = new LinkedHashMap<>();

    static {
        for (final RequestType requestType : values()) {
            BY_NAME_MAP.put(requestType.name().toUpperCase(Locale.ROOT), requestType);
        }
    }

    public static Optional<RequestType> getByName(final String name) {
        return UtilString.isEmpty(name) ? Optional.empty() : Optional.ofNullable(BY_NAME_MAP.get(name.toUpperCase(Locale.ROOT)));
    }
}