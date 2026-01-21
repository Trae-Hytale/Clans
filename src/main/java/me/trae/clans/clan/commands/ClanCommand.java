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

    public static final String NOT_IN_CLAN_MESSAGE = "You are not in a Clan.";

    private final OptionalArg<String> inputArg;

    public ClanCommand() {
        super("clan", "Clan management");

        this.addAliases("c");
        this.addAliases("faction", "fac", "f");

        this.inputArg = this.withOptionalArg("input", "Search by Player or Clan name", ArgTypes.STRING);
    }

    @Override
    protected void execute(@Nonnull final CommandContext commandContext, @Nonnull final Store<EntityStore> store, @Nonnull final Ref<EntityStore> ref, @Nonnull final PlayerRef playerRef, @Nonnull final World world) {
        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

        if (!(commandContext.provided(this.inputArg))) {
            if (playerClanOptional.isEmpty()) {
                UtilMessage.message(playerRef, "Clans", NOT_IN_CLAN_MESSAGE);
                return;
            }

            final Clan playerClan = playerClanOptional.get();

            this.showClanInformation(playerRef, playerClan, playerClan);
            return;
        }

        final String input = commandContext.get(this.inputArg);

        final Optional<Clan> targetClanOptional = this.getManager().getClanByName(input);
        if (targetClanOptional.isEmpty()) {
            UtilMessage.message(playerRef, "Clans", "The clan <yellow>%s</yellow> was not found".formatted(input));
            return;
        }

        final Clan targetClan = targetClanOptional.get();

        this.showClanInformation(playerRef, playerClanOptional.orElse(null), targetClan);
    }

    private void showClanInformation(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        UtilMessage.message(playerRef, "Clans", "%s Information:".formatted(this.getManager().getClanName(this.getManager().getClanRelationByClan(playerClan, targetClan), targetClan)));
    }
}