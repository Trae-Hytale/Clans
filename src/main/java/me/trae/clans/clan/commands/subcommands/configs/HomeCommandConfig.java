package me.trae.clans.clan.commands.subcommands.configs;

import io.github.trae.di.configuration.annotations.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Configuration("Clan/Commands/HomeCommand")
public class HomeCommandConfig {

    private long cooldown = Duration.ofMinutes(5).toMillis();

    private long wildernessTeleportDuration = Duration.ofSeconds(30).toMillis();
    private long selfTerritoryTeleportDuration = 0L;
    private long allianceTerritoryTeleportDuration = Duration.ofSeconds(15).toMillis();
    private long pillageTerritoryTeleportDuration = this.wildernessTeleportDuration;

    private boolean onlyTeleportFromSpawn = false;
}