package me.trae.clans.clan.configs;

import io.github.trae.di.configuration.annotations.Configuration;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Configuration("Clan/Energy")
public class EnergyConfig {

    private boolean enabled = true;

    private long defaultEnergy = Duration.ofDays(1).toMillis();

    private List<String> alertIntervals = List.of("24h", "12h", "6h", "2h", "1h", "45m", "30m", "15m", "10m", "5m", "3m", "2m", "1m");
}