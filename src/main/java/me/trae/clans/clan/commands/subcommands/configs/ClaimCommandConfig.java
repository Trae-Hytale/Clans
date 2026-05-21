package me.trae.clans.clan.commands.subcommands.configs;

import io.github.trae.di.configuration.annotations.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Configuration("Clan/Commands/ClaimCommand")
public class ClaimCommandConfig {

    private String outlineBlockId = "Build_Lightsource_Yellow";
}