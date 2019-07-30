package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "music_link")
@Data
@NoArgsConstructor
public class MusicLinkEntity {

    @Id
    @Column(name = "music_link_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "link")
    private String link;

    @ManyToMany
    @JoinTable (name="playlist_to_music_link",
            joinColumns=@JoinColumn (name="music_link_id"),
            inverseJoinColumns=@JoinColumn(name="playlist_id"))
    private List<MusicLinkEntity> playlists;

}
