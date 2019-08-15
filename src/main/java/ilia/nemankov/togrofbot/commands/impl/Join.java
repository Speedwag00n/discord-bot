package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.EmotionAudioLoader;
import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
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
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            if (channel == null) {
                return resources.getString("error.connection.no_chosen_voice_channel");
            } else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
                return resources.getString("error.permissions.join_voice_channel");
            } else {
                AudioManager audioManager = event.getGuild().getAudioManager();
                if (audioManager.isAttemptingToConnect()) {
                    return resources.getString("error.connection.try_to_connect");
                } else {
                    GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                    GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

                    audioManager.openAudioConnection(channel);
                    provider.getPlayerManager().loadItem("https://vimeo.com/354064901", new EmotionAudioLoader(musicManager.getTrackScheduler()));

                    return resources.getString("message.command.join.greeting");
                }
            }
        }
    }

}
