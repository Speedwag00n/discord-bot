package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.AudioLoaderInfo;
import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.StringArgumentMatcher;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.VoiceUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Play extends AbstractCommand {

    private static final String[] variants = new String[] {"play", "p"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Play() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new DefaultPlay());
        setCommandItems(commandItems);
    }

    private class DefaultPlay extends CommandItem {

        public DefaultPlay() {
            super(new ArgumentsTemplate(null, new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.play.default.args"),
                    resources.getString("description.command.play.default.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            String link = arguments.get(0).getArgument();

            AudioLoaderInfo info = new AudioLoaderInfo();

            info.setVoiceChannel(event.getMember().getVoiceState().getChannel());
            info.setGuild(event.getGuild());
            info.setCommunicationChannel(event.getMessage().getTextChannel());
            info.addLink(link);

            return VoiceUtils.playMusic(info, true);
        }
    }

}
