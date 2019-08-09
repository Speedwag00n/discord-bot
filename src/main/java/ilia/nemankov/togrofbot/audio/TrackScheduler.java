package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter implements CommunicationScheduler {

    private static final Logger logger = LoggerFactory.getLogger(TrackScheduler.class);

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private AudioTrack playingNow;
    private TextChannel communicationChannel;
    private String playlist;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
            logger.debug("Added a track with identifier \"{}\" to the queue", track.getIdentifier());
        } else {
            //Track started playing because the queue was empty
            if (communicationChannel != null)
                communicationChannel.sendMessage("Started play \"" + track.getInfo().title + "\"").queue();
            playingNow = track;
        }
    }

    @Override
    public void next() {
        AudioTrack track = queue.poll();
        boolean result = player.startTrack(track, false);
        if (track != null) {
            if (result) {
                playingNow = track;
                logger.debug("Started a track \"{}\"", track.getIdentifier());
                if (communicationChannel != null)
                    communicationChannel.sendMessage("Started play \"" + track.getInfo().title + "\"").queue();
            } else {
                playingNow = null;
                logger.debug("Could not start a track \"{}\"", track.getIdentifier());
                if (communicationChannel != null)
                    communicationChannel.sendMessage("Failed to play \"" + track.getInfo().title + "\"").queue();
            }
        }
        else {
            playingNow = null;
            communicationChannel = null;
            playlist = null;
            logger.debug("Nothing to play");
        }
    }

    @Override
    public void clear(AudioTrack track) {
        queue.remove(track);
        logger.debug("Delete track with identifier {} from scheduler queue", track.getIdentifier());
    }

    @Override
    public void clearAll() {
        queue.clear();
        logger.debug("Track scheduler queue cleared");
    }

    @Override
    public boolean isEmpty() {
        return (queue.size() == 0);
    }

    @Override
    public AudioTrack getPlayingNow() {
        return playingNow;
    }

    public TextChannel getCommunicationChannel() {
        return communicationChannel;
    }

    public void setCommunicationChannel(TextChannel communicationChannel) {
        this.communicationChannel = communicationChannel;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            logger.debug("Ended a track \"{}\". Starting next", track.getIdentifier());
            next();
        }
    }

    public String getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

}