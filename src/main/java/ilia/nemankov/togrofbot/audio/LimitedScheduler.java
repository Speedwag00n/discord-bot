package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public interface LimitedScheduler extends Scheduler {

    void queue(AudioTrack track, long limit);

    void setLimitMillis(long limit);
    long getLimitMillis();

}
