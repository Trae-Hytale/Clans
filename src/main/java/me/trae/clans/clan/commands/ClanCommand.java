package me.trae.clans.clan.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Module;
import me.trae.framework.utility.UtilMessage;

import javax.annotation.Nonnull;
import java.util.Optional;

@Component
public class ClanCommand extends AbstractPlayerCommand implements Module<Clans, ClanManager> {

    private final OptionalArg<String> nameArg;

    public ClanCommand() {
        super("clan", "Clan management");

        this.addAliases("c");
        this.addAliases("faction", "fac", "f");

        this.nameArg = this.withOptionalArg("name", "Provide a name to search", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world) {
        if (commandContext.provided(this.nameArg)) {
            this.getManager().getClanByName(commandContext.get(this.nameArg)).ifPresent(targetClan -> {
                final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

                this.displayClan(playerRef, playerClanOptional.orElse(null), targetClan);
            });
            return;
        }

        this.getManager().getClanByPlayer(playerRef).ifPresentOrElse(playerClan -> this.displayClan(playerRef, playerClan, playerClan), () -> this.sendAbsentClanMessage(playerRef));
    }

    private void displayClan(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
    }

    public void sendAbsentClanMessage(final PlayerRef playerRef) {
        UtilMessage.message(playerRef, "Clans", "You are not in a Clan.");
    }

    public void sendPresentClanMessage(final PlayerRef playerRef) {
        UtilMessage.message(playerRef, "Clans", "You are already in a Clan.");
    }
}