package me.trae.clans.fields.configs;

import io.github.trae.di.configuration.annotations.Comment;
import io.github.trae.di.configuration.annotations.Configuration;
import io.github.trae.utilities.UtilJava;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.util.LinkedHashMap;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Configuration("Fields")
public class FieldsConfig {

    private boolean enabled = true;

    private boolean insertLootIntoInventory = true;

    @Comment("Leave as 0 to use dynamicDurations")
    private long staticDuration = 0L;

    private LinkedHashMap<Integer, Long> dynamicDurations = UtilJava.createMap(new LinkedHashMap<>(), map -> {
        map.put(25, Duration.ofMinutes(60).toMillis());
        map.put(50, Duration.ofMinutes(45).toMillis());
        map.put(75, Duration.ofMinutes(30).toMillis());
        map.put(100, Duration.ofMinutes(15).toMillis());
        map.put(150, Duration.ofMinutes(10).toMillis());
        map.put(Integer.MAX_VALUE, Duration.ofMinutes(5).toMillis());
    });
}