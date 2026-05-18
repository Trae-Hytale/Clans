package me.trae.clans.fields.configs;

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
@Configuration("Fields")
public class FieldsConfig {

    private boolean enabled = true;

    private long duration = Duration.ofMinutes(10).toMillis();
}