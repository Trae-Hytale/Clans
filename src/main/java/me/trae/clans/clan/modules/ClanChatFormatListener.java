package me.trae.clans.clan.modules;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import me.trae.clans.Clans;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.core.chat.events.ChatReceiveEvent;
import me.trae.core.client.Client;
import me.trae.framework.base.annotations.Component;
import me.trae.framework.base.wrappers.Module;
import me.trae.framework.event.Listener;
import me.trae.framework.event.annotations.EventHandler;
import me.trae.framework.utility.enums.ChatColor;

@Component
public class ClanChatFormatListener implements Module<Clans, ClanManager>, Listener {

    @EventHandler
    public void onChatReceive(final ChatReceiveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final PlayerRef player = event.getPlayer();
        final Client playerClient = event.getPlayerClient();

        final PlayerRef targetPlayer = event.getTargetPlayer();

        final String content = event.getContent();

        this.getManager().getClanByPlayer(player).ifPresent(playerClan -> {
            event.setFormat(this.buildFormat(player, playerClient, targetPlayer, playerClan, content));
        });
    }

    private Message buildFormat(final PlayerRef player, final Client playerClient, final PlayerRef targetPlayer, final Clan playerClan, final String content) {
        final ClanRelation clanRelation = this.getManager().getClanRelationByPlayer(targetPlayer, player);

        final Message separator = Message.raw(" ");

        final Message rank = playerClient.getRank().getPrefix();
        final Message clanName = Message.raw(playerClan.getName()).color(clanRelation.getPrefix());
        final Message playerName = Message.raw(player.getUsername()).color(clanRelation.getSuffix());
        final Message message = Message.raw(content).color(ChatColor.WHITE.getColor());

        return Message.join(rank, clanName, separator, playerName, separator, message);
    }
}