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
@Configuration("Clan/Request")
public class RequestConfig {

    private long expiration = Duration.ofMinutes(5).toMillis();
    private boolean inform = true;
}