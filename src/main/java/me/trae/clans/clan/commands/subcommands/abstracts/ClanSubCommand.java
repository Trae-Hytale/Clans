package me.trae.clans.clan.commands.subcommands.abstracts;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.enums.ClanConditionType;
import me.trae.framework.base.wrappers.SubModule;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class ClanSubCommand extends AbstractPlayerCommand implements SubModule<Clans, ClanCommand> {

    public ClanSubCommand(@Nonnull final String name, @Nonnull final String description) {
        super(name, description);
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world) {
        final Optional<Clan> playerClanOptional = this.getModule().getManager().getClanByPlayer(playerRef);

        switch (this.getClanConditionType()) {
            case PRESENT -> {
                if (playerClanOptional.isEmpty()) {
                    this.getModule().sendAbsentClanMessage(playerRef);
                    return;
                }
                break;
            }
            case ABSENT -> {
                if (playerClanOptional.isPresent()) {
                    this.getModule().sendPresentClanMessage(playerRef);
                    return;
                }
                break;
            }
        }

        this.execute(commandContext, store, ref, playerRef, world, playerClanOptional.orElse(null));
    }

    protected ClanConditionType getClanConditionType() {
        return ClanConditionType.PRESENT;
    }

    protected abstract void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world, final Clan playerClan);
}