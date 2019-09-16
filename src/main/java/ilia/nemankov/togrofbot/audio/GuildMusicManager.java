package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

public class GuildMusicManager implements MusicManager {

    private final AudioPlayer audioPlayer;
    private final TrackScheduler trackScheduler;

    public GuildMusicManager(AudioPlayerManager manager) {
        audioPlayer = manager.createPlayer();
        trackScheduler = new TrackScheduler(audioPlayer);
        audioPlayer.addListener(trackScheduler);
    }

    @Override
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    @Override
    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    @Override
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(audioPlayer, trackScheduler);
    }

}
