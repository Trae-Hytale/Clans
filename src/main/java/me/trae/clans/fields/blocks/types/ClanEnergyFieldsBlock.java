package me.trae.clans.fields.blocks.types;

import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.utilities.UtilTime;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.events.energy.ClanEnergyGainEvent;
import me.trae.clans.fields.blocks.FieldsBlock;
import me.trae.clans.fields.loot.Loot;
import me.trae.core.event.BlockBreakEvent;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Component
public class ClanEnergyFieldsBlock implements FieldsBlock {

    @Override
    public List<String> getBlockIds() {
        return List.of("Rock_Crystal_Purple_Large", "Rock_Crystal_Purple_Medium", "Rock_Crystal_Purple_Small");
    }

    @Override
    public List<Loot> getLootList() {
        return List.of(
                new Loot() {
                    @Override
                    public void apply(final BlockBreakEvent blockBreakEvent) {
                        final PlayerRef playerRef = blockBreakEvent.getPlayerRef();

                        ClanEnergyFieldsBlock.this.getEnergy(blockBreakEvent.getBlockType().getId()).ifPresent(energy -> {
                            final ClanManager clanManager = this.getFieldsManager().getClanManager();

                            clanManager.getClanByPlayer(playerRef).ifPresent(playerClan -> {
                                UtilEvent.dispatch(new ClanEnergyGainEvent(playerClan, energy));

                                UtilMessage.message(playerRef, "Fields", "You received <light_purple>%s</light_purple> of Clan Energy.".formatted(UtilTime.getTime(energy)));
                            });
                        });
                    }
                }
        );
    }

    @Override
    public String getReplacementBlockId() {
        return BlockType.EMPTY.getId();
    }

    private Optional<Long> getEnergy(final String blockId) {
        return switch (blockId) {
            case "Rock_Crystal_Purple_Large" -> Optional.of(Duration.ofHours(1).toMillis());
            case "Rock_Crystal_Purple_Medium" -> Optional.of(Duration.ofMinutes(30).toMillis());
            case "Rock_Crystal_Purple_Small" -> Optional.of(Duration.ofMinutes(15).toMillis());
            default -> Optional.empty();
        };
    }
}