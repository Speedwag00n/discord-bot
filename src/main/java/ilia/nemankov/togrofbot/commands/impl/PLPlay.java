package ilia.nemankov.togrofbot.commands.impl;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.audio.MusicAudioLoader;
import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.entity.VideoInfo;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.LinkUtils;
import ilia.nemankov.togrofbot.util.MessageUtils;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

public class PLPlay implements Command {

    private static final Logger logger = LoggerFactory.getLogger(PLPlay.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "PLPlay <playlist> - The bot starts play tracks from the specified playlist" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        String response;
        try {
            String playlist = event.getMessage().getContentRaw().split("\\s+")[1];

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> playlistEntities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

            if (playlistEntities.isEmpty()) {
                response = resources.getString("message.command.playlist.not_found");
            } else {
                List<MusicLinkEntity> musicLinkEntities = playlistEntities.get(0).getLinks();

                if (channel == null) {
                    response = resources.getString("error.connection.no_chosen_voice_channel");
                } else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
                    response = resources.getString("error.permissions.join_voice_channel");
                } else {
                    AudioManager audioManager = event.getGuild().getAudioManager();
                    if (audioManager.isAttemptingToConnect()) {
                        response = resources.getString("error.connection.try_to_connect");
                    } else {
                        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                        GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

                        musicManager.getAudioPlayer().stopTrack();
                        musicManager.getTrackScheduler().clearAll();

                        audioManager.openAudioConnection(channel);

                        AudioLoadResultHandler audioLoader = new MusicAudioLoader(musicManager.getTrackScheduler());
                        musicManager.getTrackScheduler().setPlaylist(playlist);

                        musicManager.getTrackScheduler().setCommunicationChannel(event.getChannel());
                        for (MusicLinkEntity musicLinkEntity : musicLinkEntities) {
                            VideoInfo info = new VideoInfo(musicLinkEntity.getIdentifier(), musicLinkEntity.getSource(), musicLinkEntity.getTitle());
                            AudioTrack track;
                            if ((track = LinkUtils.buildAudioTrack(info)) != null) {
                                audioLoader.trackLoaded(track);
                            }
                        }

                        response = null;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            response = MessageFormat.format(
                    resources.getString("error.argument.empty"),
                    MessageUtils.capitalizeFirstLetter(resources.getString("arguments.name_of_playlist"))
            );
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        if (response != null)
            event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
