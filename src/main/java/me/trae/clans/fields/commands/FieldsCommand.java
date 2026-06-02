package me.trae.clans.fields.commands;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.utility.UtilColor;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.utilities.UtilString;
import me.trae.clans.ClansPlugin;
import me.trae.clans.fields.FieldsBlock;
import me.trae.clans.fields.FieldsManager;
import me.trae.core.client.enums.Rank;
import me.trae.core.command.Command;
import me.trae.core.command.SubCommand;

@Component
public class FieldsCommand extends Command<ClansPlugin, FieldsManager, PlayerRef> {

    public FieldsCommand() {
        super("fields", "Fields management", Rank.ADMIN);
    }

    @Override
    public void execute(final PlayerRef playerRef, final String[] args) {
    }

    @Component
    private static class InfoCommand extends SubCommand<ClansPlugin, FieldsCommand, PlayerRef> {

        public InfoCommand() {
            super("info", "Fields information", Rank.ADMIN);
        }

        @Override
        public void execute(final PlayerRef playerRef, final String[] args) {
            UtilMessage.message(playerRef, "Fields", "Information:");
            UtilMessage.message(playerRef, UtilString.pair("Remaining Blocks", UtilColor.serialize(ChatColor.YELLOW.getColor(), String.valueOf(this.getModule().getManager().getRemainingFieldsBlockList().size()))));
            UtilMessage.message(playerRef, UtilString.pair("Broken Blocks", UtilColor.serialize(ChatColor.YELLOW.getColor(), String.valueOf(this.getModule().getManager().getBrokenFieldsBlockList().size()))));
        }
    }

    @Component
    private static class ResetCommand extends SubCommand<ClansPlugin, FieldsCommand, PlayerRef> {

        public ResetCommand() {
            super("reset", "Reset all Fields Blocks", Rank.ADMIN);
        }

        @Override
        public void execute(final PlayerRef playerRef, final String[] args) {
            final int count = this.getModule().getManager().getBrokenFieldsBlockList().size();

            this.getModule().getManager().reset();

            UtilMessage.message(playerRef, "Fields", "Restored <yellow>%s</yellow> Fields Blocks.".formatted(count));
        }
    }

    @Component
    private static class PurgeCommand extends SubCommand<ClansPlugin, FieldsCommand, PlayerRef> {

        public PurgeCommand() {
            super("purge", "Purge all Fields Blocks", Rank.OWNER);
        }

        @Override
        public void execute(final PlayerRef playerRef, final String[] args) {
            int count = 0;

            for (final FieldsBlock fieldsBlock : this.getModule().getManager().getFieldsBlockList()) {
                this.getModule().getManager().removeFieldsBlock(fieldsBlock);
                this.getModule().getManager().getRepository().delete(fieldsBlock);
                count++;
            }

            UtilMessage.message(playerRef, "Fields", "Deleted <yellow>%s</yellow> Fields Blocks.".formatted(count));
        }
    }
}