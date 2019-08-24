package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@NamedEntityGraphs(
    {
        @NamedEntityGraph(
            name = "playlist-entity.without-links",
            attributeNodes = {
                @NamedAttributeNode("id"),
                @NamedAttributeNode("name"),
                @NamedAttributeNode("guildId")
            }
        ),
        @NamedEntityGraph(
            name = "playlist-entity.with-links",
            attributeNodes = {
                @NamedAttributeNode("id"),
                @NamedAttributeNode("name"),
                @NamedAttributeNode("guildId"),
                @NamedAttributeNode(value = "links", subgraph = "links-subgraph")
            },
            subgraphs = {
                @NamedSubgraph(
                    name = "links-subgraph",
                    attributeNodes = {
                        @NamedAttributeNode("identifier"),
                        @NamedAttributeNode("source"),
                        @NamedAttributeNode("title"),
                        @NamedAttributeNode("creationDatetime")
                    }
                )
            }
        )
    }
)
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

    @OneToMany (mappedBy="playlist", fetch=FetchType.LAZY)
    @OrderBy("creation_datetime asc")
    private List<MusicLinkEntity> links;

}
