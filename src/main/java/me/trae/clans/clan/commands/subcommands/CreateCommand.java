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
import me.trae.clans.clan.commands.subcommands.enums.ClanConditionType;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.SubModule;
import me.trae.framework.utility.UtilMessage;
import me.trae.framework.utility.UtilServer;

import javax.annotation.Nonnull;

@Component
public class CreateCommand extends ClanSubCommand implements SubModule<Clans, ClanCommand> {

    private final RequiredArg<String> nameArg;

    protected CreateCommand() {
        super("create", "Create a Clan");

        this.nameArg = this.withRequiredArg("name", "Provide a Clan name", ArgTypes.STRING);
    }

    @Override
    protected ClanConditionType getClanConditionType() {
        return ClanConditionType.ABSENT;
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world, final Clan playerClan) {
        final String name = commandContext.get(this.nameArg);
        if (name == null) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Name to Create.");
            return;
        }

        if (this.getModule().getSubCommands().containsKey(name.toLowerCase())) {
            UtilMessage.message(playerRef, "Clans", "You cannot use that name!");
            return;
        }

        if (this.getModule().getManager().getClanByName(name).isPresent()) {
            UtilMessage.message(playerRef, "Clans", "That name is already used by another clan!");
            return;
        }

        final Clan clan = new Clan(playerRef, name);

        this.getModule().getManager().addClan(clan);
        this.getModule().getManager().getRepository().saveData(clan);

        for (final PlayerRef target : UtilServer.getOnlinePlayers()) {
            final ClanRelation clanRelation = this.getModule().getManager().getClanRelationByPlayer(target, playerRef);

            UtilMessage.message(target, "Clans", "%s has formed %s.".formatted(this.getModule().getManager().getPlayerName(clanRelation, playerRef), this.getModule().getManager().getClanFullName(clanRelation, clan)));
        }
    }
}