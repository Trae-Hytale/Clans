package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
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
import me.trae.clans.clan.events.clan.ClanCreateEvent;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;

import java.util.Locale;

@Component
public class CreateCommand extends AbstractClanSubCommand implements EventListener {

    private final CreateCommandConfig createCommandConfig;

    public CreateCommand(final CreateCommandConfig createCommandConfig) {
        super("create", "Create a Clan");

        this.createCommandConfig = createCommandConfig;
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_EMPTY;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (args.length == 0) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Name to Create.");
            return;
        }

        final String clanName = args[0];

        if (!(this.canCreateClan(playerRef, client, clanName))) {
            return;
        }

        UtilEvent.dispatch(new ClanCreateEvent(playerRef, client, clanName));
    }

    private boolean canCreateClan(final PlayerRef playerRef, final Client client, final String name) {
        if (this.getModule().getAbstractCommand().getSubCommands().containsKey(name.toLowerCase(Locale.ROOT))) {
            UtilMessage.message(playerRef, "Clans", "You cannot use that as your Clan name!");
            return false;
        }

        final String cleanName = client.isAdministrating() ? name.replace("_", "") : name;

        if (!(cleanName.matches(this.createCommandConfig.getNameRegex()))) {
            UtilMessage.message(playerRef, "Clans", "You cannot have special characters in your Clan name!");
            return false;
        }

        if (this.getModule().getManager().getClanByName(name).isPresent()) {
            UtilMessage.message(playerRef, "Clans", "Clan name is already used by another clan!");
            return false;
        }

        if (name.length() > this.createCommandConfig.getMaximumNameLength()) {
            UtilMessage.message(playerRef, "Clans", "Clan name is too long. Maximum Length is <yellow>%s</yellow>!".formatted(this.createCommandConfig.getMaximumNameLength()));
            return false;
        }

        if (!(client.isAdministrating())) {
            if (name.length() < this.createCommandConfig.getMinimumNameLength()) {
                UtilMessage.message(playerRef, "Clans", "Clan name is too short. Minimum Length is <yellow>%s</yellow>!".formatted(this.createCommandConfig.getMinimumNameLength()));
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanCreate(final ClanCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final PlayerRef playerRef = event.getPlayerRef();

        final Clan clan = new Clan(playerRef, event.getName(), this.getModule().getManager().getEnergyConfig().getDefaultEnergy());

        if (event.getPlayerClient().isAdministrating() && event.getPlayerClient().hasRank(Rank.OWNER)) {
            clan.setAdmin(true);
        }

        this.getModule().getManager().addClan(clan);
        this.getModule().getManager().getRepository().save(clan);

        if (this.createCommandConfig.isBroadcastMessage()) {
            for (final PlayerRef targetPlayerRef : Universe.get().getPlayers()) {
                final ClanRelation clanRelation = this.getModule().getManager().getClanRelationByClan(this.getModule().getManager().getClanByPlayer(targetPlayerRef).orElse(null), clan);

                UtilMessage.message(targetPlayerRef, "Clans", "%s formed %s.".formatted(this.getModule().getManager().getPlayerName(clanRelation, playerRef), this.getModule().getManager().getClanFullName(clanRelation, clan)));
            }
        } else {
            UtilMessage.message(playerRef, "Clans", "You formed %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.SELF, clan)));
        }
    }
}