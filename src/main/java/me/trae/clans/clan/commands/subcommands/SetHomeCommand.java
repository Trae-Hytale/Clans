package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.clan.ClanSetHomeEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;
import java.util.Optional;

@Component
public class SetHomeCommand extends AbstractClanSubCommand implements Listener {

    public SetHomeCommand() {
        super("sethome", "Set Clan Home");
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
        final World world = player.getWorld();
        if (world == null) {
            return;
        }

        final BlockLocation blockLocation = BlockLocation.of(world, playerRef.getTransform().getPosition().toVector3i());

        if (!(this.canSetHome(playerRef, playerClan, blockLocation))) {
            return;
        }

        UtilEvent.dispatch(new ClanSetHomeEvent(playerClan, playerRef, blockLocation));
    }

    private boolean canSetHome(final PlayerRef playerRef, final Clan playerClan, final BlockLocation blockLocation) {
        final Optional<Clan> territoryClanOptional = this.getModule().getManager().getClanByLocation(blockLocation);
        if (territoryClanOptional.isEmpty() || !(territoryClanOptional.get().equals(playerClan))) {
            UtilMessage.message(playerRef, "Clans", "You can only set home in your own territory!");
            return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanSetHome(final ClanSetHomeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final BlockLocation blockLocation = event.getBlockLocation();

        clan.setHome(blockLocation);
        this.getModule().getManager().getRepository().update(clan, ClanProperty.HOME);

        UtilMessage.message(playerRef, "Clans", "You set the Clan Home at %s.".formatted(clan.getFormattedHomeLocation()));

        this.getModule().getManager().messageClan(clan, "Clans", "%s has set the Clan Home at %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), clan.getFormattedHomeLocation()), Collections.singletonList(playerRef.getUuid()));
    }
}