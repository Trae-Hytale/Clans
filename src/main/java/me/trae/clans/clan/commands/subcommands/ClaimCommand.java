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
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanClaimEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.blockrestore.BlockRestoreManager;
import me.trae.core.client.Client;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Component
public class ClaimCommand extends AbstractClanSubCommand implements Listener {

    private final BlockRestoreManager blockRestoreManager;

    public ClaimCommand(final BlockRestoreManager blockRestoreManager) {
        super("claim", "Claim Territory");

        this.blockRestoreManager = blockRestoreManager;
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

        final Chunk chunk = Chunk.of(world, playerRef.getTransform().getPosition());

        if (!(this.canClaimChunk(playerRef, client, playerClan, chunk))) {
            return;
        }

        UtilEvent.dispatch(new ClanClaimEvent(playerClan, playerRef, chunk));
    }

    private boolean canClaimChunk(final PlayerRef playerRef, final Client client, final Clan playerClan, final Chunk chunk) {
        final Optional<Clan> territoryClanOptional = this.getModule().getManager().getClanByChunk(chunk);
        if (territoryClanOptional.isPresent()) {
            final Clan territoryClan = territoryClanOptional.get();

            if (territoryClan.equals(playerClan)) {
                UtilMessage.message(playerRef, "Clans", "This Territory is already owned by your Clan!");
                return false;
            }

            UtilMessage.message(playerRef, "Clans", "This Territory is owned by %s!".formatted(this.getModule().getManager().getClanName(this.getModule().getManager().getClanRelationByClan(playerClan, territoryClan), territoryClan)));
            return false;
        }

        if (!(client.isAdministrating())) {
            if (!(chunk.getWorldName().equals(this.getModule().getManager().getConfig().getTerritory().allowedWorldName()))) {
                UtilMessage.message(playerRef, "Clans", "You cannot claim land in this world!");
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanClaim(final ClanClaimEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Clan clan = event.getClan();
        final PlayerRef playerRef = event.getPlayerRef();
        final Chunk chunk = event.getChunk();

        clan.addTerritory(chunk);
        this.getModule().getManager().getRepository().update(clan, ClanProperty.TERRITORY);

        this.getModule().getManager().getClanChunkStorage().update(chunk, chunk, clan);

        this.blockRestoreManager.outlineChunk(chunk, "Clan:%s".formatted(clan.getName()), "Build_Lightsource_Yellow", Duration.ofMinutes(5).toMillis());

        final String chunkString = "(<yellow>%s</yellow>, <yellow>%s</yellow>)".formatted(chunk.getX(), chunk.getZ());

        UtilMessage.message(playerRef, "Clans", "You claimed territory at %s.".formatted(chunkString));

        this.getModule().getManager().messageClan(clan, "Clans", "%s has claimed territory at %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), chunkString), Collections.singletonList(playerRef.getUuid()));
    }
}