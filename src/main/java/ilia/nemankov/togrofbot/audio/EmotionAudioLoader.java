package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmotionAudioLoader extends AbstractAudioLoader implements AudioLoadResultHandler {

    private static final Logger logger = LoggerFactory.getLogger(EmotionAudioLoader.class);

    public EmotionAudioLoader(Scheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        if (getScheduler().isEmpty() && (getScheduler().getPlayingNow() == null)) {
            getScheduler().queue(track);
            logger.debug("Started playing emotion \"{}\"", track.getIdentifier());
        } else {
            logger.debug("Scheduler queue isn't empty, could not play emotion \"{}\"", track.getIdentifier());
        }
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        for (AudioTrack track : playlist.getTracks()) {
            getScheduler().queue(track);
        }
    }

    @Override
    public void noMatches() {

    }

    @Override
    public void loadFailed(FriendlyException throwable) {
        logger.debug("Failed to load bot audio emotion", throwable);
    }

}
