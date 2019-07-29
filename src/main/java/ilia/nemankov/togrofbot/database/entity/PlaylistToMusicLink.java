package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "playlist_to_music_link")
@Data
@NoArgsConstructor
public class PlaylistToMusicLink implements Serializable {

    private static final long serialVersionUID = -8804419849147303716L;

    @Id
    @Column(name = "playlist_id")
    private long playlistId;

    @Id
    @Column(name = "music_link_id")
    private long musicLinkId;

}
