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
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.utility.UtilMessage;

import javax.annotation.Nonnull;

@Component
public class InviteCommand extends ClanSubCommand implements SubModule<Clans, ClanCommand> {

    private final RequiredArg<String> nameArg;

    public InviteCommand() {
        super("invite", "Invite a Player");

        this.nameArg = this.withRequiredArg("name", "Provide a Player name", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world, final Clan playerClan) {
        UtilMessage.message(playerRef, "Clans", "You tried to invite a player, still in progress...");
    }
}