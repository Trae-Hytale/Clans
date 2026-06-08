package me.trae.clans.fields.commands;

import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import io.github.trae.di.InjectorApi;
import io.github.trae.di.annotations.type.component.Component;
import io.github.trae.hytale.framework.command.impl.Confirmable;
import io.github.trae.hytale.framework.utility.UtilColor;
import io.github.trae.hytale.framework.utility.UtilMessage;
import io.github.trae.hytale.framework.utility.enums.ChatColor;
import io.github.trae.utilities.UtilString;
import me.trae.clans.ClansPlugin;
import me.trae.clans.fields.FieldsData;
import me.trae.clans.fields.FieldsManager;
import me.trae.clans.fields.configs.FieldsConfig;
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

            UtilMessage.message(playerRef, UtilString.pair("Remaining Blocks", UtilColor.serialize(ChatColor.YELLOW.getColor(), String.valueOf(this.getModule().getManager().getRemainingDataList().size()))));
            UtilMessage.message(playerRef, UtilString.pair("Broken Blocks", UtilColor.serialize(ChatColor.YELLOW.getColor(), String.valueOf(this.getModule().getManager().getBrokenDataList().size()))));
        }
    }

    @Component
    private static class ResetCommand extends SubCommand<ClansPlugin, FieldsCommand, PlayerRef> {

        public ResetCommand() {
            super("reset", "Reset all Fields Blocks", Rank.ADMIN);
        }

        @Override
        public void execute(final PlayerRef playerRef, final String[] args) {
            final int count = this.getModule().getManager().getBrokenDataList().size();

            this.getModule().getManager().replenish();

            UtilMessage.message(playerRef, "Fields", "Restored <yellow>%s</yellow> Fields Blocks.".formatted(count));
        }
    }

    @Component
    private static class PurgeCommand extends SubCommand<ClansPlugin, FieldsCommand, PlayerRef> implements Confirmable {

        public PurgeCommand() {
            super("purge", "Purge all Fields Blocks", Rank.OWNER);
        }

        @Override
        public void execute(final PlayerRef playerRef, final String[] args) {
            int count = 0;

            for (final FieldsData fieldsData : this.getModule().getManager().getData()) {
                this.getModule().getManager().removeData(fieldsData);
                this.getModule().getManager().getRepository().delete(fieldsData);

                count++;
            }

            UtilMessage.message(playerRef, "Fields", "Deleted <yellow>%s</yellow> Fields Blocks.".formatted(count));
        }

        @Override
        public void sendConfirmationMessage(final CommandSender commandSender) {
            UtilMessage.message(commandSender, "Fields", "<red>Run the command again to confirm puring all fields blocks!</red>");
        }
    }

    @Component
    private static class AutoPickupCommand extends SubCommand<ClansPlugin, FieldsCommand, PlayerRef> {

        public AutoPickupCommand() {
            super("autopickup", "Toggle Auto Pickup", Rank.ADMIN);
        }

        @Override
        public void execute(final PlayerRef playerRef, final String[] args) {
            final FieldsConfig fieldsConfig = this.getModule().getManager().getFieldsConfig();

            if (fieldsConfig.isInsertLootIntoInventory()) {
                fieldsConfig.setInsertLootIntoInventory(false);

                InjectorApi.saveConfiguration(fieldsConfig.getClass());

                UtilMessage.message(playerRef, "Fields", UtilString.pair("Auto Pickup", "<red>Off</red>"));
            } else {
                fieldsConfig.setInsertLootIntoInventory(true);

                InjectorApi.saveConfiguration(fieldsConfig.getClass());

                UtilMessage.message(playerRef, "Fields", UtilString.pair("Auto Pickup", "<green>On</green>"));
            }
        }
    }
}