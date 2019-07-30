package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "playlist")
@Data
@NoArgsConstructor
public class PlaylistEntity {

    @Id
    @Column(name = "playlist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "playlist_name")
    private String name;

    @Column(name = "guild_id")
    private long guildId;

    @ManyToMany
    @JoinTable (name="playlist_to_music_link",
            joinColumns=@JoinColumn (name="playlist_id"),
            inverseJoinColumns=@JoinColumn(name="music_link_id"))
    private List<MusicLinkEntity> links;

}
