package ilia.nemankov.togrofbot.commands.impl;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Now extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Now.class);

    private static final String[] variants = new String[] {"now"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Now() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new DefaultNow());
        setCommandItems(commandItems);
    }

    private class DefaultNow extends CommandItem {

        public DefaultNow() {
            super(new ArgumentsTemplate(null));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.now.default.args"),
                    resources.getString("description.command.now.default.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
            GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

            AudioTrack track = musicManager.getTrackScheduler().getPlayingNow();
            String playlist = musicManager.getTrackScheduler().getPlaylist();

            if (track == null) {
                return resources.getString("message.command.now.empty");
            }
            if (playlist != null) {
                return MessageFormat.format(
                        resources.getString("message.command.now.track_and_playlist"),
                        track.getInfo().title,
                        playlist
                );
            } else {
                return MessageFormat.format(
                        resources.getString("message.command.now.track"),
                        track.getInfo().title
                );
            }
        }
    }

}
