package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface Scheduler {

    void queue(AudioTrack track);

    void next();

    void clear(AudioTrack track);

    void clearAll();

    boolean isEmpty();

    AudioTrack getPlayingNow();

}
