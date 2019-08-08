package ilia.nemankov.togrofbot.commands.impl;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Music implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Music.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] {"music <playlist> - Show the first page of all tracks list for specified playlist",
                "music <playlist> <page> - Show the <page> of all tracks list for specified playlist"};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        String response;
        try {
            String playlist = event.getMessage().getContentRaw().split("\\s+")[1];

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

            if (entities.isEmpty()) {
                response = "Playlist with specified name not found";
            } else {
                List<String> links = entities.get(0).getLinks().parallelStream().map(entity -> entity.getLink()).collect(Collectors.toList());

                try {
                    int page = Integer.parseInt(event.getMessage().getContentRaw().split("\\s+")[2]);
                    if (links.isEmpty()) {
                        response = "There isn't any track in playlist \"" + playlist + "\"";
                    } else if ((page > 0) && (links.size() / 10 + ((links.size() % 10 == 0) ? 0 : 1)) >= page) {
                        response = showPage(page, links);
                    } else {
                        response = "This page for command " + this.getClass().getSimpleName() + " not found";
                    }
                } catch (NumberFormatException e) {
                    response = "Argument must be a number";
                } catch (ArrayIndexOutOfBoundsException e) {
                    if (links.isEmpty()) {
                        response = "There isn't any track in playlist \"" + playlist + "\"";
                    } else {
                        response = showPage(1, links);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            response = "Name of playlist must be presented";
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

    private String showPage(int pageNumber, List<String> tracks) {
        StringBuilder responseBuilder = new StringBuilder();

        YoutubeAudioSourceManager sourceManager = new YoutubeAudioSourceManager();
        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
        responseBuilder.append(pageNumber + " of " + (tracks.size() / 10 + ((tracks.size() % 10 == 0) ? 0 : 1)) + " page:\n");
        for (int i = (pageNumber - 1) * 10; i < ((tracks.size() > pageNumber * 10) ? pageNumber * 10 : tracks.size()); i++) {
            AudioItem item = sourceManager.loadItem((DefaultAudioPlayerManager) provider.getPlayerManager(), new AudioReference(tracks.get(i), null));
            YoutubeAudioTrack track = (YoutubeAudioTrack)item;

            responseBuilder.append((i + 1) + ") " + track.getInfo().title + "\n");
        }
        return responseBuilder.toString();
    }

}
