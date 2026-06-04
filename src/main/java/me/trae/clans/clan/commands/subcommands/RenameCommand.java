package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.commands.subcommands.configs.CreateCommandConfig;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.clan.ClanRenameEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;

import java.util.Collections;
import java.util.Locale;

@Component
public class RenameCommand extends AbstractClanSubCommand implements EventListener {

    private final CreateCommandConfig createCommandConfig;

    public RenameCommand(final CreateCommandConfig createCommandConfig) {
        super("rename", "Rename the Clan Name", Rank.ADMIN);

        this.createCommandConfig = createCommandConfig;
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_PRESENT;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (args.length == 0) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Name to Rename.");
            return;
        }

        final String name = args[0];

        if (!(this.canRenameClan(playerRef, name))) {
            return;
        }

        UtilEvent.dispatch(new ClanRenameEvent(playerClan, playerRef, name));
    }

    private boolean canRenameClan(final PlayerRef playerRef, final String name) {
        if (this.getModule().getAbstractCommand().getSubCommands().containsKey(name.toLowerCase(Locale.ROOT))) {
            UtilMessage.message(playerRef, "Clans", "You cannot use that as the Clan name!");
            return false;
        }

        final String cleanName = name.replace("_", "");

        if (!(cleanName.matches(this.createCommandConfig.getNameRegex()))) {
            UtilMessage.message(playerRef, "Clans", "You cannot have special characters in the Clan name!");
            return false;
        }

        if (this.getModule().getManager().getClanByName(name).isPresent()) {
            UtilMessage.message(playerRef, "Clans", "Clan name is already used by another clan!");
            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanRename(final ClanRenameEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final String name = event.getName();

        this.getModule().getManager().getClanNameStorage().update(clan.getName(), name, clan);

        clan.setName(name);
        this.getModule().getManager().getRepository().update(clan, ClanProperty.NAME);

        UtilMessage.message(playerRef, "Clans", "You renamed the Clan Name to %s.".formatted(this.getModule().getManager().getClanShortName(ClanRelation.SELF, clan)));

        this.getModule().getManager().messageClan(clan, "Clans", "%s has renamed the Clan Name to %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanShortName(ClanRelation.SELF, clan)), Collections.singletonList(playerRef.getUuid()));
    }
}