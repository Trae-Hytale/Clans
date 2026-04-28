package me.trae.clans.clan.configs;

import io.github.trae.di.configuration.annotations.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Configuration("Clans")
public class ClansConfig {

    private Territory territory = new Territory(8, "default");
    private CreateCommand createCommand = new CreateCommand(3, 14, "^[A-Za-z0-9]+$");

    public record Territory(int maxClaimLimit, String allowedWorldName) {}

    public record CreateCommand(int minNameLength, int maxNameLength, String nameRegex) {}
}