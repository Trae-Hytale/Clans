package me.trae.clans.clan.listeners.sidebar;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.sidebar.events.SidebarUpdateEvent;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilPlayer;
import io.github.trae.hytale.framework.wrappers.Chunk;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.events.clan.*;
import me.trae.clans.clan.events.member.MemberKickEvent;
import me.trae.clans.clan.events.member.MemberLeaveEvent;
import me.trae.clans.clan.events.territory.TerritoryChangeEvent;
import me.trae.clans.clan.events.territory.TerritoryClaimEvent;
import me.trae.clans.clan.events.territory.TerritoryUnClaimAllEvent;
import me.trae.clans.clan.events.territory.TerritoryUnClaimEvent;
import me.trae.clans.gamer.events.GamerCoinsUpdateEvent;

@Component
public class ClansSidebarUpdateListener implements Module<ClansPlugin, ClanManager>, Listener {

    private void update(final PlayerRef playerRef) {
        UtilEvent.dispatch(new SidebarUpdateEvent("CLANS", playerRef));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onGamerCoinsUpdate(final GamerCoinsUpdateEvent event) {
        this.update(event.getGamer().getPlayerRef());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTerritoryChange(final TerritoryChangeEvent event) {
        this.update(event.getPlayerRef());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanCreate(final ClanCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.update(event.getPlayerRef());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanDisband(final ClanDisbandEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.update(event.getPlayerRef());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanJoin(final ClanJoinEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.update(event.getPlayerRef());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberLeave(final MemberLeaveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.update(event.getPlayerRef());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMemberKick(final MemberKickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.update(event.getTargetClient().getPlayerRef());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTerritoryClaim(final TerritoryClaimEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (final Player player : event.getChunk().getEntitiesByType(Player.class)) {
            UtilPlayer.getPlayerRef(player).ifPresent(this::update);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTerritoryUnClaim(final TerritoryUnClaimEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (final Player player : event.getChunk().getEntitiesByType(Player.class)) {
            UtilPlayer.getPlayerRef(player).ifPresent(this::update);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTerritoryUnClaimAll(final TerritoryUnClaimAllEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (final Chunk chunk : event.getChunks()) {
            for (final Player player : chunk.getEntitiesByType(Player.class)) {
                UtilPlayer.getPlayerRef(player).ifPresent(this::update);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanNeutral(final ClanNeutralEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.getClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
        event.getTargetClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanAlly(final ClanAllyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.getClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
        event.getTargetClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanTrust(final ClanTrustEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.getClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
        event.getTargetClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanUnTrust(final ClanUnTrustEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.getClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
        event.getTargetClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClanEnemy(final ClanEnemyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        event.getClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
        event.getTargetClan().getMembers().values().stream().map(Member::getPlayerRef).forEach(this::update);
    }
}