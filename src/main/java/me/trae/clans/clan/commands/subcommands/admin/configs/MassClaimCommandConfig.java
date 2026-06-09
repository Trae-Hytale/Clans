package me.trae.clans.clan.commands.subcommands.admin.configs;

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
@Configuration("Clan/Commands/Admin/MassClaimCommand")
public class MassClaimCommandConfig {

    private boolean outlineEnabled = true;

    private boolean drawOutlineForEachChunk = true;

    private String outlineBlockId = "Build_Lightsource_Red";

    private long outlineDuration = Duration.ofMinutes(5).toMillis();
}