package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MusicAudioLoader extends AbstractAudioLoader implements AudioLoadResultHandler {

    public MusicAudioLoader(CommunicationScheduler scheduler) {
        super(scheduler);
    }

    @Override
    public void trackLoaded(AudioTrack track) {
        getScheduler().queue(track);
        log.debug("Loaded \"{}\"", track.getIdentifier());
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
        log.debug("Failed to load track", throwable);
    }

}
