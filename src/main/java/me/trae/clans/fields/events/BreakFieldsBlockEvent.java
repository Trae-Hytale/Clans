package me.trae.clans.fields.events;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.hytale.framework.event.types.CustomCancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.trae.clans.fields.FieldsData;
import me.trae.clans.fields.blocks.FieldsBlock;

@AllArgsConstructor
@Getter
public class BreakFieldsBlockEvent extends CustomCancellableEvent {

    private final FieldsData fieldsData;
    private final FieldsBlock fieldsBlock;
    private final PlayerRef playerRef;
}