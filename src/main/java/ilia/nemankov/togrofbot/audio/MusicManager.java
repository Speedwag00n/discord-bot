package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import net.dv8tion.jda.core.audio.AudioSendHandler;

public interface MusicManager {

    AudioPlayer getAudioPlayer();

    AudioEventAdapter getTrackScheduler();

    AudioSendHandler getSendHandler();

}
