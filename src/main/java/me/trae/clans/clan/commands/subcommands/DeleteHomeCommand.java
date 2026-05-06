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
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.clan.ClanDeleteHomeEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;

@Component
public class DeleteHomeCommand extends AbstractClanSubCommand implements EventListener {

    public DeleteHomeCommand() {
        super("delhome", "Delete Clan Home");

        this.addAliases("deletehome");
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_PRESENT;
    }

    @Override
    public MemberRole getRequiredMemberRole() {
        return MemberRole.ADMIN;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (!(this.canDeleteHome(playerRef, playerClan))) {
            return;
        }

        UtilEvent.dispatch(new ClanDeleteHomeEvent(playerClan, playerRef));
    }

    private boolean canDeleteHome(final PlayerRef playerRef, final Clan playerClan) {
        if (!(playerClan.hasHome())) {
            UtilMessage.message(playerRef, "Clans", "Your Clan does not have a home set!");
            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanDeleteHome(final ClanDeleteHomeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();

        clan.setHome(null);
        this.getModule().getManager().getRepository().update(clan, ClanProperty.HOME);

        UtilMessage.message(playerRef, "Clans", "You deleted the Clan Home.");

        this.getModule().getManager().messageClan(clan, "Clans", "%s has deleted the Clan Home.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef)), Collections.singletonList(playerRef.getUuid()));
    }
}