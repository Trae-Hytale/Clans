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
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Module;
import me.trae.framework.utility.UtilJava;
import me.trae.framework.utility.UtilMessage;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Optional;

@Component
public class ClanCommand extends AbstractPlayerCommand implements Module<Clans, ClanManager> {

    private final OptionalArg<String> nameArg = this.withOptionalArg("name", "Provide a name to search", ArgTypes.STRING);

    protected ClanCommand() {
        super("clan", "Clan management");

        this.addAliases("c");
        this.addAliases("faction", "fac", "f");
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world) {
        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

        if (commandContext.provided(this.nameArg)) {
            final String name = commandContext.get(this.nameArg);

            this.getManager().searchClan(playerRef, name, true).ifPresent(targetClan -> this.displayClan(playerRef, playerClanOptional.orElse(null), targetClan));
        } else {
            playerClanOptional.ifPresentOrElse(playerClan -> {
                this.displayClan(playerRef, playerClan, playerClan);
            }, () -> {
                this.sendAbsentClanMessage(playerRef);
            });
        }
    }

    private void displayClan(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        final ClanRelation clanRelation = this.getManager().getClanRelationByClan(playerClan, targetClan);

        UtilMessage.message(playerRef, "Clans", "%s Information:".formatted(this.getManager().getClanShortName(clanRelation, targetClan)));

        final LinkedHashMap<String, String> informationMap = UtilJava.updateMap(new LinkedHashMap<>(), map -> {
            map.put("ID", targetClan.getId().toString());
        });
    }

    public void sendAbsentClanMessage(final PlayerRef playerRef) {
        UtilMessage.message(playerRef, "Clans", "You are not in a Clan.");
    }

    public void sendPresentClanMessage(final PlayerRef playerRef) {
        UtilMessage.message(playerRef, "Clans", "You are already in a Clan.");
    }
}