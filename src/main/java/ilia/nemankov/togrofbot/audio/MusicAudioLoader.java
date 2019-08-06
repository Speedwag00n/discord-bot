package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MusicAudioLoader implements AudioLoadResultHandler {

    private static final Logger logger = LoggerFactory.getLogger(MusicAudioLoader.class);

    private TrackScheduler trackScheduler;

    public MusicAudioLoader(TrackScheduler trackScheduler) {
        this.trackScheduler = trackScheduler;
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        trackScheduler.queue(track);
        logger.debug("Started playing track \"{}\"", track.getIdentifier());
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack track : playlist.getTracks()) {
            trackScheduler.queue(track);
        }
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException throwable) {
        logger.debug("Failed to load track", throwable);
    }

}