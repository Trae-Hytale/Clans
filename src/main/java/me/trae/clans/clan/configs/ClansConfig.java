package me.trae.clans.clan.configs;

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
@Configuration("Clans")
public class ClansConfig {

    private Squad squad = new Squad(8);
    private Territory territory = new Territory(4, "default");
    private Pillage pillage = new Pillage(Duration.ofMinutes(10).toMillis());

    private CreateCommand createCommand = new CreateCommand(3, 14, "^[A-Za-z0-9]+$");
    private LeaveCommand leaveCommand = new LeaveCommand(true);
    private KickCommand kickCommand = new KickCommand(true);

    public record Squad(int maxLimit) {}

    public record Territory(int maxClaimLimit, String allowedWorldName) {}

    public record Pillage(long duration) {}

    public record CreateCommand(int minNameLength, int maxNameLength, String nameRegex) {}

    public record LeaveCommand(boolean pillageCheck) {}

    public record KickCommand(boolean pillageCheck) {}
}