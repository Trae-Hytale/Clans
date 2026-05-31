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
@Configuration("Clan/Commands/DisbandCommand")
public class DisbandCommandConfig {

    private boolean broadcastMessage = true;
}