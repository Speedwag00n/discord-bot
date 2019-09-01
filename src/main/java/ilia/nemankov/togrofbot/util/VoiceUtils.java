package ilia.nemankov.togrofbot.util;

import ilia.nemankov.togrofbot.audio.*;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.ResourceBundle;

@Slf4j
public class VoiceUtils {

    public enum AudioType {

        MUSIC, EMOTION

    }

    public static String playMusic(AudioLoaderInfo info, boolean append) {
        return playAudio(info, append, AudioType.MUSIC);
    }

    public static String playEmotion(AudioLoaderInfo info) {
        return playAudio(info, false, AudioType.EMOTION);
    }

    private static String playAudio(AudioLoaderInfo info, boolean append, AudioType type) {
        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        Guild guild = info.getGuild();

        VoiceChannel channel = info.getVoiceChannel();
        if (channel == null) {
            return resources.getString("error.connection.no_chosen_voice_channel");
        } else if (!guild.getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
            return resources.getString("error.permissions.join_voice_channel");
        }

        AudioManager audioManager = guild.getAudioManager();

        if (audioManager.isAttemptingToConnect()) {
            return resources.getString("error.connection.try_to_connect");
        }

        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
        GuildMusicManager musicManager = provider.getGuildMusicManager(guild);

        if (!append) {
            musicManager.getAudioPlayer().stopTrack();
            musicManager.getTrackScheduler().clearAll();
        }

        audioManager.openAudioConnection(channel);

        musicManager.getTrackScheduler().setCommunicationChannel(info.getCommunicationChannel());

        switch(type) {
            case MUSIC:
                for (String link : info.getLinks()) {
                    provider.getPlayerManager().loadItem(link, new MusicAudioLoader(musicManager.getTrackScheduler()));
                }
                break;
            case EMOTION:
                for (String link : info.getLinks()) {
                    provider.getPlayerManager().loadItem(link, new EmotionAudioLoader(musicManager.getTrackScheduler()));
                }
                break;
                default:
                    log.error("Unexpected audio type {}", type);
                    break;
        }

        return null;
    }

    public static void setPlayingPlaylist(Guild guild, String playlist) {
        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
        GuildMusicManager musicManager = provider.getGuildMusicManager(guild);

        musicManager.getTrackScheduler().setPlaylist(playlist);
    }

}
