package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer audioPlayer;
    private AudioFrame lastFrame;
    private long framesLimit;
    private long sentFramesCount;
    private LimitedScheduler scheduler;

    public AudioPlayerSendHandler(AudioPlayer audioPlayer, LimitedScheduler scheduler) {
        this.audioPlayer = audioPlayer;
        this.scheduler = scheduler;
        framesLimit = scheduler.getLimitMillis() / 20;
    }

    @Override
    public boolean canProvide() {
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }
        return lastFrame != null;
    }

    @Override
    public byte[] provide20MsAudio() {
        if (framesLimit != 0) {
            if (framesLimit == sentFramesCount) {
                scheduler.next();
            } else {
                sentFramesCount++;
            }
        }
        if (lastFrame == null) {
            lastFrame = audioPlayer.provide();
        }
        byte[] data = lastFrame != null ? lastFrame.getData() : null;
        lastFrame = null;

        return data;
    }

    @Override
    public boolean isOpus() {
        return true;
    }

}
