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
@Configuration("Clan/Commands/CreateCommand")
public class CreateCommandConfig {

    private int minimumNameLength = 3;
    private int maximumNameLength = 14;

    private String nameRegex = "^[A-Za-z0-9]+$";
}