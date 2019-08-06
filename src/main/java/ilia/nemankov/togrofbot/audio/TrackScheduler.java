package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private static final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private AudioTrack playingNow;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            logger.debug("Added a track \"{}\" to the queue", track.getIdentifier());
        } else {
            //Track started playing because the queue was empty
            playingNow = track;
        }
    }

    public void nextTrack() {
        AudioTrack track = queue.poll();
        boolean result = player.startTrack(track, false);
        if (track != null) {
            if (result) {
                playingNow = track;
                logger.debug("Started a track \"{}\"", track.getIdentifier());
            } else {
                playingNow = null;
                logger.debug("Could not start a track \"{}\"", track.getIdentifier());
            }
        }
        else {
            playingNow = null;
            logger.debug("Nothing to play");
        }
    }

    public void clearAll() {
        queue.clear();
        logger.debug("Track scheduler queue cleared");
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            logger.debug("Ended a track \"{}\". Starting next", track.getIdentifier());
            nextTrack();
        }
    }

    public boolean isEmpty() {
        return (queue.size() == 0);
    }

    public AudioTrack getPlayingNow() {
        return playingNow;
    }

}