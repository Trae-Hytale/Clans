package me.trae.clans.clan.listeners.chat;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import lombok.AllArgsConstructor;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.core.chat.enums.CoreChatChannel;
import me.trae.core.chat.events.ChatReceiveEvent;
import me.trae.core.chat.events.abstracts.AbstractChatEvent;

@AllArgsConstructor
@Component
public class ChatFormatForClanListener implements Module<ClansPlugin, ClanManager>, EventListener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChatReceive(final ChatReceiveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getChannel().equals(CoreChatChannel.GLOBAL))) {
            return;
        }

        final PlayerRef sender = event.getSender();

        this.getManager().getClientManager().getClientByPlayer(sender).ifPresent(client -> this.getManager().getClanByPlayer(sender).ifPresent(clan -> {
            final PlayerRef recipient = event.getRecipient();

            final ClanRelation clanRelation = this.getManager().getClanRelationByClan(this.getManager().getClanByPlayer(recipient).orElse(null), clan);

            final Message rankPrefix = client.getRank().getPrefix();
            final Message clanName = Message.raw(clan.getDisplayName()).color(clanRelation.getPrefix());
            final Message username = AbstractChatEvent.USERNAME_FORMAT.apply(client.getName(), clanRelation.getSuffix());
            final Message content = AbstractChatEvent.CONTENT_FORMAT.apply(event.getContent(), ChatColor.WHITE.getColor());

            event.setFormat(Message.join(rankPrefix, clanName, Message.raw(" "), username, AbstractChatEvent.SEPARATOR, content));
        }));
    }
}