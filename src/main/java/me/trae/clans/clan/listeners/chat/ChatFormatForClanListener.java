package me.trae.clans.clan.listeners.chat;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hf.Module;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import lombok.AllArgsConstructor;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.core.chat.events.ChatReceiveEvent;
import me.trae.core.chat.events.abstracts.AbstractChatEvent;

@AllArgsConstructor
@Component
public class ChatFormatForClanListener implements Module<ClansPlugin, ClanManager>, Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChatReceive(final ChatReceiveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final PlayerRef sender = event.getSender();

        this.getManager().getClientManager().getClientByPlayer(sender).ifPresent(client -> this.getManager().getClanByPlayer(sender).ifPresent(clan -> {
            final PlayerRef recipient = event.getRecipient();

            final ClanRelation clanRelation = this.getManager().getClanRelationByClan(this.getManager().getClanByPlayer(recipient).orElse(null), clan);

            event.setFormat(Message.join(client.getRank().getPrefix(), Message.raw(clan.getDisplayName()).color(clanRelation.getPrefix()), Message.raw(" "), AbstractChatEvent.USERNAME_FORMAT.apply(client.getName(), clanRelation.getSuffix()), AbstractChatEvent.SEPARATOR, AbstractChatEvent.CONTENT_FORMAT.apply(event.getContent())));
        }));
    }
}