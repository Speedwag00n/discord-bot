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
import ilia.nemankov.togrofbot.database.entity.VideoInfo;
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
        logger.debug("Video for link {} not found", link);
        return null;
    }

    public static AudioTrack buildAudioTrack(VideoInfo videoInfo) {
        String source = videoInfo.getSource();
        AudioItem item;
        switch (source) {
            case "youtube":
                YoutubeAudioSourceManager youtubeAudioSourceManager = new YoutubeAudioSourceManager();
                item = youtubeAudioSourceManager.loadTrackWithVideoId(videoInfo.getIdentifier(), false);
                break;
            case "vimeo":
                VimeoAudioSourceManager vimeoAudioSourceManager = new VimeoAudioSourceManager();
                GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                item = vimeoAudioSourceManager.loadItem((DefaultAudioPlayerManager)provider.getPlayerManager(),
                        new AudioReference(videoInfo.getIdentifier(), null));
                break;
                default:
                    logger.warn("Unknown source for video");
                    return null;
        }
        if (item != null && item instanceof AudioTrack) {
            logger.debug("AudioTrack object built");
            return (AudioTrack)item;
        } else {
            logger.debug("Could not build AudioTrack object");
            return null;
        }
    }

    public static String buildLink(VideoInfo videoInfo) {
        String link;
        switch (videoInfo.getSource()) {
            case "youtube":
                link = "https://www.youtube.com/watch?v=" + videoInfo.getIdentifier();
                break;
            case "vimeo":
                link = videoInfo.getIdentifier();
                break;
                default:
                    logger.error("Unknown source for video");
                    return null;
        }
        logger.debug("Built link {}", link);
        return link;
    }

}
