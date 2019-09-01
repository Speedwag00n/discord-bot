package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Leave extends AbstractCommand {

    private static final String[] variants = new String[] {"leave", "l"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Leave() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new DefaultLeave());
        setCommandItems(commandItems);
    }

    private class DefaultLeave extends CommandItem {

        public DefaultLeave() {
            super(new ArgumentsTemplate(null));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.leave.default.args"),
                    resources.getString("description.command.leave.default.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            AudioManager audioManager = event.getGuild().getAudioManager();
            if (audioManager.getConnectedChannel() == null) {
                return resources.getString("message.command.leave.not_in_channel");
            } else {
                if (audioManager.isAttemptingToConnect()) {
                    return resources.getString("error.connection.try_to_connect");
                } else {
                    GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                    GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

                    musicManager.getAudioPlayer().stopTrack();
                    musicManager.getTrackScheduler().clearAll();

                    audioManager.closeAudioConnection();
                    return resources.getString("message.command.leave.successful");
                }
            }
        }
    }

}
