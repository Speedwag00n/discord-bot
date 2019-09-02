package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.audio.TrackScheduler;
import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Skip extends AbstractCommand {

    private static final String[] variants = new String[] {"skip", "s"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Skip() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new DefaultSkip());
        setCommandItems(commandItems);
    }

    private class DefaultSkip extends CommandItem {

        public DefaultSkip() {
            super(new ArgumentsTemplate(null));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.skip.default.args"),
                    resources.getString("description.command.skip.default.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
            GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

            TrackScheduler trackScheduler = musicManager.getTrackScheduler();

            if (trackScheduler.getPlayingNow() == null) {
                return resources.getString("message.command.skip.nothing");
            } else {
                trackScheduler.next();
                return null;
            }
        }
    }

}
