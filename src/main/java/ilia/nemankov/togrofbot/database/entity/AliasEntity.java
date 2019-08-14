package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "playlist")
@Data
@NoArgsConstructor
public class AliasEntity {

    @Id
    @Column(name = "alias_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "alias_name")
    private String name;

    @Column(name = "guild_id")
    private long guildId;

    @Column(name = "command")
    private String command;

}
