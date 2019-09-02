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
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class LinkUtils {

    public static VideoInfo parseLink(String link) {
        log.debug("Started parse link {}", link);
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
                        log.debug("Found video with identifier {} in source {}", track.getIdentifier(), track.getInfo());
                        return new VideoInfo(track.getIdentifier(), sourceManager.getSourceName(), track.getInfo().title);
                    } else {
                        log.warn("Identifier {} or title {} is empty", track.getIdentifier(), track.getInfo());
                    }
                } else {
                    log.debug("Loaded item isn't an AudioTrack instance");
                }
            } catch (FriendlyException e) {
                log.debug("Can't find video in source {}", sourceManager.getSourceName());
            }
        }
        log.debug("Video for link {} not found", link);
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
                    log.warn("Unknown source for video");
                    return null;
        }
        if (item != null && item instanceof AudioTrack) {
            log.debug("AudioTrack object built");
            return (AudioTrack)item;
        } else {
            log.debug("Could not build AudioTrack object");
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
                    log.error("Unknown source for video");
                    return null;
        }
        log.debug("Built link {}", link);
        return link;
    }

}
