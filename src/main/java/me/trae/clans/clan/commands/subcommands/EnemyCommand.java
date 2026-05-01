package me.trae.clans.clan.commands.subcommands;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.event.Listener;
import io.github.trae.hytale.framework.event.annotations.EventHandler;
import io.github.trae.hytale.framework.event.constants.EventPriority;
import io.github.trae.hytale.framework.utility.UtilEvent;
import io.github.trae.hytale.framework.utility.UtilMessage;
import me.trae.clans.clan.Clan;
import me.trae.clans.clan.commands.subcommands.abstracts.AbstractClanSubCommand;
import me.trae.clans.clan.commands.subcommands.abstracts.enums.ClanStateRequirement;
import me.trae.clans.clan.data.Enemy;
import me.trae.clans.clan.data.enums.MemberRole;
import me.trae.clans.clan.enums.ClanRelation;
import me.trae.clans.clan.events.ClanEnemyEvent;
import me.trae.clans.clan.properties.ClanProperty;
import me.trae.core.client.Client;

import java.util.Collections;

@Component
public class EnemyCommand extends AbstractClanSubCommand implements Listener {

    public EnemyCommand() {
        super("enemy", "Enemy a Clan");
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
        if (args.length == 0) {
            UtilMessage.message(playerRef, "Clans", "You did not input a Clan to Enemy.");
            return;
        }

        this.getModule().getManager().searchClan(playerRef, args[0], true).ifPresent(targetClan -> {
            if (!(this.canEnemyClan(playerRef, client, playerClan, targetClan))) {
                return;
            }

            UtilEvent.dispatch(new ClanEnemyEvent(playerClan, playerRef, client, targetClan));
        });
    }

    private boolean canEnemyClan(final PlayerRef playerRef, final Client client, final Clan playerClan, final Clan targetClan) {
        if (targetClan.equals(playerClan)) {
            UtilMessage.message(playerRef, "Clans", "You cannot enemy yourself!");
            return false;
        }

        if (targetClan.isEnemyByClan(playerClan)) {
            UtilMessage.message(playerRef, "Clans", "You are already enemies with %s!".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ENEMY, targetClan)));
            return false;
        }

        if (!(targetClan.isNeutralByClan(playerClan))) {
            UtilMessage.message(playerRef, "Clans", "You must be neutral with %s to wage war!".formatted(this.getModule().getManager().getClanFullName(this.getModule().getManager().getClanRelationByClan(playerClan, targetClan), targetClan)));
            return false;
        }

        if (!(client.isAdministrating())) {
            if (targetClan.isAdmin()) {
                UtilMessage.message(playerRef, "Clans", "You cannot enemy Admin Clans!");
                return false;
            }
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanEnemy(final ClanEnemyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.handleEnemy(event.getPlayerRef(), event.getClan(), event.getTargetClan());
    }

    private void handleEnemy(final PlayerRef playerRef, final Clan playerClan, final Clan targetClan) {
        playerClan.addEnemy(new Enemy(targetClan));
        this.getModule().getManager().getRepository().update(playerClan, ClanProperty.ENEMIES);

        targetClan.addEnemy(new Enemy(playerClan));
        this.getModule().getManager().getRepository().update(targetClan, ClanProperty.ENEMIES);

        UtilMessage.message(playerRef, "Clans", "You waged war with %s.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ENEMY, targetClan)));

        this.getModule().getManager().messageClan(playerClan, "Clans", "%s has waged war with %s.".formatted(this.getModule().getManager().getPlayerName(ClanRelation.SELF, playerRef), this.getModule().getManager().getClanFullName(ClanRelation.ENEMY, targetClan)), Collections.singletonList(playerRef.getUuid()));
        this.getModule().getManager().messageClan(targetClan, "Clans", "%s has waged war with your Clan.".formatted(this.getModule().getManager().getClanFullName(ClanRelation.ENEMY, playerClan)), null);
    }
}