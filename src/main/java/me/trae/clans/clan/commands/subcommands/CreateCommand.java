package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.ClanSubCommand;
import me.trae.clans.clan.commands.subcommands.enums.ClanRequirement;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.utility.UtilMessage;

import javax.annotation.Nonnull;

@Component
public class CreateCommand extends ClanSubCommand implements SubModule<Clans, ClanCommand> {

    private final RequiredArg<String> nameArg;

    public CreateCommand() {
        super("create", "Create a Clan");

        this.nameArg = this.withRequiredArg("name", "Choose a Clan name", ArgTypes.STRING);
    }

    @Override
    protected ClanRequirement getClanRequirement() {
        return ClanRequirement.NULL;
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world, final Clan playerClan) {
        if (!(commandContext.provided(this.nameArg))) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Name to Create.");
            return;
        }

        final String name = commandContext.get(this.nameArg);

        if (!(this.canCreateClan(playerRef, name))) {
            return;
        }

        UtilMessage.message(playerRef, "Clans", "You tried to create a clan, still in progress...");
    }

    private boolean canCreateClan(final PlayerRef playerRef, final String name) {
        if (this.getModule().getSubCommands().containsKey(name.toLowerCase())) {
            UtilMessage.message(playerRef, "Clans", "You cannot use that as a clan name!");
            return false;
        }

        if (this.getModule().getManager().getClanByName(name).isPresent()) {
            UtilMessage.message(playerRef, "Clans", "That Clan name is already owned by another clan!");
            return false;
        }

        return true;
    }
}