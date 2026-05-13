package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.utilities.UtilTime;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.commands.subcommands.configs.HomeCommandConfig;
import me.trae.clans.clan.events.clan.ClanHomeEvent;
import me.trae.clans.clan.teleport.ClanHomeTeleportData;
import me.trae.core.client.Client;
import me.trae.core.teleport.TeleportManager;

@Component
public class HomeCommand extends AbstractClanSubCommand implements EventListener {

    private static final String COOLDOWN_NAME = "Clan Home Command";

    private final HomeCommandConfig homeCommandConfig;
    private final TeleportManager teleportManager;

    public HomeCommand(final HomeCommandConfig homeCommandConfig, final TeleportManager teleportManager) {
        super("home", "Teleport to Clan Home");

        this.homeCommandConfig = homeCommandConfig;
        this.teleportManager = teleportManager;
    }

    @Override
    public ClanStateRequirement getRequiredState() {
        return ClanStateRequirement.CLAN_PRESENT;
    }

    @Override
    public void execute(final PlayerRef playerRef, final Player player, final Client client, final Clan playerClan, final String[] args) {
        if (!(this.canTeleportHome(playerRef, client, playerClan))) {
            return;
        }

        UtilEvent.dispatch(new ClanHomeEvent(playerClan, playerRef, player, playerClan.getHome()));
    }

    private boolean canTeleportHome(final PlayerRef playerRef, final Client client, final Clan playerClan) {
        if (!(playerClan.hasHome())) {
            UtilMessage.message(playerRef, "Clans", "Your Clan does not have a home set!");
            return false;
        }

        if (!(client.isAdministrating())) {
            if (this.teleportManager.getTeleportByPlayerRef(playerRef).map(teleportData -> teleportData instanceof ClanHomeTeleportData).orElse(false)) {
                UtilMessage.message(playerRef, "Clans", "You are already teleporting to Clan Home!");
                return false;
            }

            if (this.getModule().getManager().getCooldownManager().isCooling(playerRef, COOLDOWN_NAME, true)) {
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanHome(final ClanHomeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final ClanHomeTeleportData clanHomeTeleportData = new ClanHomeTeleportData(event.getClan(), event.getPlayerRef(), event.getPlayer(), event.getBlockLocation(), 0L);

        clanHomeTeleportData.setPreConsumer(teleportData -> {
            if (!(teleportData.isInstant())) {
                UtilMessage.message(teleportData.getPlayerRef(), "Clans", "You will be teleported to Clan Home in <green>%s</green>.".formatted(UtilTime.getTime(teleportData.getDuration())));
            }
        });

        clanHomeTeleportData.setPostConsumer(teleportData -> {
            this.getModule().getManager().getCooldownManager().add(event.getPlayerRef(), COOLDOWN_NAME, this.homeCommandConfig.getCooldown(), true, true);

            UtilMessage.message(teleportData.getPlayerRef(), "Clans", "You have teleported to Clan Home.");
        });

        this.teleportManager.teleport(clanHomeTeleportData);
    }
}