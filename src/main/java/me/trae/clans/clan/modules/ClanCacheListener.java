package me.trae.clans.clan.modules;

import me.trae.clans.Clans;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.events.*;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Module;
import me.trae.framework.event.Listener;
import me.trae.framework.event.annotations.EventHandler;
import me.trae.framework.event.constants.EventPriority;
import me.trae.framework.utility.objects.Chunk;

@Component
public class ClanCacheListener implements Module<Clans, ClanManager>, Listener {

    @EventHandler(priority = EventPriority.FINISHED)
    public void onClanRename(final ClanRenameEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().updateNameInClanCache(event.getPreviousName(), event.getClan());
    }

    @EventHandler(priority = EventPriority.FINISHED)
    public void onClanJoin(final ClanJoinEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().updatePlayerInClanCache(event.getPlayer().getUuid(), event.getClan());
    }

    @EventHandler(priority = EventPriority.FINISHED)
    public void onClanLeave(final ClanLeaveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().updatePlayerInClanCache(event.getPlayer().getUuid(), null);
    }

    @EventHandler(priority = EventPriority.FINISHED)
    public void onClanKick(final ClanKickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().updatePlayerInClanCache(event.getTarget().getUuid(), null);
    }

    @EventHandler(priority = EventPriority.FINISHED)
    public void onClanClaim(final ClanClaimEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().updateChunkInClanCache(event.getChunk(), event.getClan());
    }

    @EventHandler(priority = EventPriority.FINISHED)
    public void onClanUnClaim(final ClanUnClaimEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.getManager().updateChunkInClanCache(event.getChunk(), null);
    }

    @EventHandler(priority = EventPriority.FINISHED)
    public void onClanUnClaimAll(final ClanUnClaimAllEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (final Chunk chunk : event.getChunks()) {
            this.getManager().updateChunkInClanCache(chunk, null);
        }
    }
}