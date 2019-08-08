package ilia.nemankov.togrofbot.util;

import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class LinkUtils {

    private static final Logger logger = LoggerFactory.getLogger(LinkUtils.class);

    public static VideoInfo parseLink(String link) {
        logger.debug("Started parse link {}", link);
        DefaultAudioPlayerManager playerManager = (DefaultAudioPlayerManager) GuildMusicManagerProvider.getInstance().getPlayerManager();

        List<AudioSourceManager> sourceManagers = new ArrayList<>();
        sourceManagers.add(new YoutubeAudioSourceManager());
        sourceManagers.add(new VimeoAudioSourceManager());
        for (AudioSourceManager sourceManager : sourceManagers) {
            try {
                AudioItem item = sourceManager.loadItem(playerManager, new AudioReference(link, null));

                if (item instanceof AudioTrack) {
                    AudioTrack track = (AudioTrack)item;

                    if (track.getIdentifier() != null && track.getInfo().title != null) {
                        logger.debug("Found video with identifier {} in source {}", track.getIdentifier(), track.getInfo());
                        return new VideoInfo(track.getIdentifier(), sourceManager.getSourceName(), track.getInfo().title);
                    } else {
                        logger.warn("Identifier {} or title {} is empty", track.getIdentifier(), track.getInfo());
                    }
                } else {
                    logger.debug("Loaded item isn't an AudioTrack instance");
                }
            } catch (FriendlyException e) {
                logger.debug("Can't find video in source {}", sourceManager.getSourceName());
            }
        }
        return null;
    }

}
