package me.trae.clans.clan.commands;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import me.trae.clans.Clans;
import me.trae.clans.clan.ClanManager;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Module;
import me.trae.framework.utility.UtilMessage;

import javax.annotation.Nonnull;

@Component
public class ClanCommand extends AbstractPlayerCommand implements Module<Clans, ClanManager> {

    public ClanCommand() {
        super("clan", "Clan management");

        this.addAliases("c");
        this.addAliases("faction", "fac", "f");
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world) {
        UtilMessage.message(playerRef, "Clans", "You are not in a Clan.");
    }
}