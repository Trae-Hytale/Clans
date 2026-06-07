package me.trae.clans.clan.commands.chat;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.EventListener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.utilities.UtilJava;
import me.trae.clans.ClansPlugin;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.ClanManager;
import me.trae.clans.clan.commands.ClanCommand;
import me.trae.clans.clan.data.Member;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.enums.ClansChatChannel;
import me.trae.core.chat.enums.CoreChatChannel;
import me.trae.core.chat.events.ChatSendEvent;
import me.trae.core.chat.events.abstracts.AbstractChatEvent;
import me.trae.core.client.Client;
import me.trae.core.command.Command;
import me.trae.core.gamer.Gamer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class AllyChatCommand extends Command<ClansPlugin, ClanManager, PlayerRef> implements EventListener {

    private static final ClansChatChannel CHAT_CHANNEL = ClansChatChannel.ALLIANCE;

    public AllyChatCommand() {
        super("allychat", "Toggle Ally Chat");

        this.addAliases("ac");
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
                gamer.updateChatChannel(CoreChatChannel.GLOBAL);
            } else {
                gamer.updateChatChannel(CHAT_CHANNEL);
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
            event.setFormat(this.getFormat(sender, clan, event.getContent()));
        });
    }

    private List<PlayerRef> getRecipients(final Clan playerClan) {
        return UtilJava.createCollection(new ArrayList<>(), list -> {
            list.addAll(playerClan.getMembers().values().stream().filter(Member::isOnline).map(Member::getPlayerRef).toList());

            for (final UUID id : playerClan.getAlliances().keySet()) {
                this.getManager().getClanById(id).ifPresent(allianceClan -> {
                    list.addAll(allianceClan.getMembers().values().stream().filter(Member::isOnline).map(Member::getPlayerRef).toList());
                });
            }
        });
    }

    private Message getFormat(final PlayerRef playerRef, final Clan playerClan, final String contentString) {
        final Message clanName = Message.raw(playerClan.getDisplayName()).color(ClanRelation.ALLIANCE.getPrefix());
        final Message username = AbstractChatEvent.USERNAME_FORMAT.apply(playerRef.getUsername(), ClanRelation.ALLIANCE.getPrefix());
        final Message content = AbstractChatEvent.CONTENT_FORMAT.apply(contentString, ClanRelation.ALLIANCE.getSuffix());

        return Message.join(clanName, Message.raw(" "), username, Message.raw(" "), content);
    }
}