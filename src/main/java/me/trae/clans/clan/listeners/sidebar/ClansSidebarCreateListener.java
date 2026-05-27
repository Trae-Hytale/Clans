package me.trae.clans.clan.listeners.sidebar;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.sidebar.events.SidebarCreateEvent;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.hytale.framework.wrappers.BlockLocation;
import io.github.trae.hytale.framework.wrappers.EntityLocation;
import io.github.trae.utilities.UtilJava;
import lombok.AllArgsConstructor;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.gamer.GamerManager;

import java.util.ArrayList;
import java.util.Optional;

@AllArgsConstructor
@Component
public class ClansSidebarCreateListener implements Module<ClansPlugin, ClanManager>, EventListener {

    private final GamerManager clansGamerManager;

    @EventHandler(priority = EventPriority.HIGH)
    public void onSidebarCreate(final SidebarCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.isCreated()) {
            return;
        }

        if (!(event.isIdentifier("CLANS"))) {
            return;
        }

        final PlayerRef playerRef = event.getPlayerRef();

        final World world = UtilWorld.getWorldByPlayerRef(playerRef).orElse(null);
        if (world == null) {
            return;
        }

        final BlockLocation blockLocation = EntityLocation.of(world, playerRef.getTransform().getPosition()).toBlockLocation();

        final Optional<Clan> territoryClanOptional = this.getManager().getClanByLocation(blockLocation);

        final Optional<Clan> playerClanOptional = this.getManager().getClanByPlayer(playerRef);

        event.setIdentifier("CLANS");
        event.setPriority(1);

        event.setTitle(Message.raw("Clans").color(ChatColor.GOLD.getColor()).bold(true));

        event.setLines(UtilJava.createCollection(new ArrayList<>(), list -> {
            list.add(Message.raw("Clan").color(ChatColor.YELLOW.getColor()).bold(true));
            list.add(playerClanOptional.map(clan -> Message.raw(clan.getDisplayName()).color(ClanRelation.SELF.getSuffix())).orElse(Message.raw("No clan")));

            if (this.getManager().getEnergyConfig().isEnabled()) {
                playerClanOptional.ifPresent(clan -> {
                    list.add(Message.empty());
                    list.add(Message.raw("Clan Energy").color(ChatColor.YELLOW.getColor()).bold(true));
                    list.add(Message.raw(clan.getFormattedEnergyRemaining()).color(ChatColor.GREEN.getColor()));
                });
            }

            list.add(Message.empty());
            list.add(Message.raw("Territory").color(ChatColor.YELLOW.getColor()).bold(true));
            list.add(this.getManager().getTerritoryClanNameForSidebar(playerClanOptional.orElse(null), territoryClanOptional.orElse(null), blockLocation));

            this.clansGamerManager.getGamerByPlayer(playerRef).ifPresent(gamer -> {
                list.add(Message.empty());
                list.add(Message.raw("Coins").color(ChatColor.YELLOW.getColor()).bold(true));
                list.add(Message.raw(gamer.getFormattedCoins()).color(ChatColor.GOLD.getColor()));
            });
        }));
    }
}