package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.AudioLoaderInfo;
import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.VoiceUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Join extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Join.class);

    private static final String[] variants = new String[] {"join", "j"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Join() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new DefaultJoin());
        setCommandItems(commandItems);
    }

    private class DefaultJoin extends CommandItem {

        public DefaultJoin() {
            super(new ArgumentsTemplate(null));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.join.default.args"),
                    resources.getString("description.command.join.default.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            AudioLoaderInfo info = new AudioLoaderInfo();

            info.setVoiceChannel(event.getMember().getVoiceState().getChannel());
            info.setGuild(event.getGuild());
            info.setCommunicationChannel(event.getMessage().getTextChannel());
            info.addLink("https://vimeo.com/354064901");

            String result = VoiceUtils.playEmotion(info);
            return result;
        }
    }

}
