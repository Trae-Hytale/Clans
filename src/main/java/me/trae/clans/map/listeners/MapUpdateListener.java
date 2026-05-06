package me.trae.clans.map.listeners;

import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.events.clan.*;
import me.trae.clans.clan.events.member.MemberKickEvent;
import me.trae.clans.clan.events.member.MemberLeaveEvent;
import me.trae.clans.clan.events.territory.TerritoryClaimEvent;
import me.trae.clans.clan.events.territory.TerritoryUnClaimAllEvent;
import me.trae.clans.clan.events.territory.TerritoryUnClaimEvent;
import me.trae.clans.map.MapManager;

@Component
public class MapUpdateListener implements Module<ClansPlugin, MapManager>, EventListener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanJoin(final ClanJoinEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().refreshPlayerClaimedChunks(event.getPlayerRef(), event.getClan());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberLeave(final MemberLeaveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().refreshPlayerClaimedChunks(event.getPlayerRef(), event.getClan());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberKick(final MemberKickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().refreshPlayerClaimedChunks(event.getPlayerRef(), event.getClan());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanDisband(final ClanDisbandEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().refreshClanMembersMap(event.getClan());
        this.getManager().invalidateClanTerritory(event.getClan());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTerritoryClaim(final TerritoryClaimEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().invalidateChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTerritoryUnClaim(final TerritoryUnClaimEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().invalidateChunk(event.getChunk());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTerritoryUnClaimAll(final TerritoryUnClaimAllEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (final Chunk chunk : event.getChunks()) {
            this.getManager().invalidateChunk(chunk);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanAlly(final ClanAllyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().invalidateClanTerritory(event.getClan());
        this.getManager().invalidateClanTerritory(event.getTargetClan());

        this.getManager().refreshClanMembersMapAgainst(event.getClan(), event.getTargetClan());
        this.getManager().refreshClanMembersMapAgainst(event.getTargetClan(), event.getClan());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanEnemy(final ClanEnemyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().invalidateClanTerritory(event.getClan());
        this.getManager().invalidateClanTerritory(event.getTargetClan());

        this.getManager().refreshClanMembersMapAgainst(event.getClan(), event.getTargetClan());
        this.getManager().refreshClanMembersMapAgainst(event.getTargetClan(), event.getClan());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanNeutral(final ClanNeutralEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().invalidateClanTerritory(event.getClan());
        this.getManager().invalidateClanTerritory(event.getTargetClan());

        this.getManager().refreshClanMembersMapAgainst(event.getClan(), event.getTargetClan());
        this.getManager().refreshClanMembersMapAgainst(event.getTargetClan(), event.getClan());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanTrust(final ClanTrustEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().invalidateClanTerritory(event.getClan());
        this.getManager().invalidateClanTerritory(event.getTargetClan());

        this.getManager().refreshClanMembersMapAgainst(event.getClan(), event.getTargetClan());
        this.getManager().refreshClanMembersMapAgainst(event.getTargetClan(), event.getClan());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanUnTrust(final ClanUnTrustEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().invalidateClanTerritory(event.getClan());
        this.getManager().invalidateClanTerritory(event.getTargetClan());

        this.getManager().refreshClanMembersMapAgainst(event.getClan(), event.getTargetClan());
        this.getManager().refreshClanMembersMapAgainst(event.getTargetClan(), event.getClan());
    }
}