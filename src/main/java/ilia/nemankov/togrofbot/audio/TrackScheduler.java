package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.TextChannel;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class TrackScheduler extends AudioEventAdapter implements CommunicationScheduler {

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
            log.debug("Added a track with identifier \"{}\" to the queue", track.getIdentifier());
        } else {
            //Track started playing because the queue was empty
            if (communicationChannel != null) {
                ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
                MessageUtils.sendTextResponse(
                        communicationChannel,
                        MessageFormat.format(
                                        resources.getString("message.scheduler.play.start"),
                                        track.getInfo().title)
                );
            }
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
                log.debug("Started a track \"{}\"", track.getIdentifier());
                if (communicationChannel != null) {
                    ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
                    MessageUtils.sendTextResponse(
                            communicationChannel,
                            MessageFormat.format(
                                    resources.getString("message.scheduler.play.start"),
                                    track.getInfo().title)
                    );
                }
            } else {
                playingNow = null;
                playlist = null;
                log.debug("Could not start a track \"{}\"", track.getIdentifier());
                if (communicationChannel != null) {
                    ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
                    MessageUtils.sendTextResponse(
                            communicationChannel,
                            MessageFormat.format(
                                    resources.getString("message.scheduler.play.failed"),
                                    track.getInfo().title)
                    );
                }
            }
        }
        else {
            playingNow = null;
            communicationChannel = null;
            playlist = null;
            log.debug("Nothing to play");
        }
    }

    @Override
    public void clear(AudioTrack track) {
        queue.remove(track);
        log.debug("Delete track with identifier {} from scheduler queue", track.getIdentifier());
    }

    @Override
    public void clearAll() {
        queue.clear();
        playingNow = null;
        playlist = null;
        log.debug("Track scheduler queue cleared");
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
            log.debug("Ended a track \"{}\". Starting next", track.getIdentifier());
            next();
        } else {
            communicationChannel = null;
        }
    }

    public String getPlaylist() {
        return playlist;
    }

    public void setPlaylist(String playlist) {
        this.playlist = playlist;
    }

}