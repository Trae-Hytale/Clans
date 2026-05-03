package me.trae.clans.clan.commands.chat;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.command.PlayerCommand;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.utilities.UtilString;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.enums.ClansChatChannel;
import me.trae.core.chat.events.ChatSendEvent;
import me.trae.core.chat.events.abstracts.AbstractChatEvent;
import me.trae.core.client.Client;
import me.trae.core.client.enums.Rank;
import me.trae.core.gamer.Gamer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ClanChatCommand extends PlayerCommand<ClansPlugin, ClanManager> implements Listener {

    private static final ClansChatChannel CHAT_CHANNEL = ClansChatChannel.CLAN_CHAT;

    public ClanChatCommand() {
        super("clanchat", "Toggle Clan Chat", Rank.DEFAULT);

        this.addAliases("cc");
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
        if (this.getManager().getClanByPlayer(playerRef).isEmpty()) {
            ClanCommand.CLAN_EMPTY_MESSAGE_CONSUMER.accept(playerRef);
            return;
        }

        final Optional<Client> clientOptional = this.getManager().getClientManager().getClientByPlayer(playerRef);
        if (clientOptional.isEmpty()) {
            return;
        }

        final Client client = clientOptional.get();

        if (args.length == 0) {
            final Optional<Gamer> gamerOptional = this.getManager().getCoreGamerManager().getGamerByPlayer(playerRef);
            if (gamerOptional.isEmpty()) {
                return;
            }

            final Gamer gamer = gamerOptional.get();

            if (gamer.getChatChannel().equals(CHAT_CHANNEL)) {
                gamer.resetChatChannel();

                UtilMessage.message(playerRef, "Clans", UtilString.pair("Clan Chat", "<red>Disabled</red>"));
            } else {
                gamer.setChatChannel(CHAT_CHANNEL);

                UtilMessage.message(playerRef, "Clans", UtilString.pair("Clan Chat", "<green>Enabled</green>"));
            }
            return;
        }

        UtilEvent.dispatchAsynchronous(new ChatSendEvent(playerRef, client, CHAT_CHANNEL, String.join(" ", args)));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onChatSend(final ChatSendEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getChannel().equals(CHAT_CHANNEL))) {
            return;
        }

        final PlayerRef sender = event.getSender();

        this.getManager().getClanByPlayer(sender).ifPresent(clan -> {
            event.setRecipients(this.getRecipients(clan));
            event.setFormat(this.getFormat(sender, event.getContent()));
        });
    }

    private List<PlayerRef> getRecipients(final Clan playerClan) {
        return new ArrayList<>(playerClan.getMembers().values().stream().filter(Member::isOnline).map(Member::getPlayerRef).toList());
    }

    private Message getFormat(final PlayerRef playerRef, final String contentString) {
        final Message username = AbstractChatEvent.USERNAME_FORMAT.apply(playerRef.getUsername(), ClanRelation.SELF.getSuffix());
        final Message content = AbstractChatEvent.CONTENT_FORMAT.apply(contentString, ClanRelation.SELF.getPrefix());

        return Message.join(username, Message.raw(" "), content);
    }
}