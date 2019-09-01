package ilia.nemankov.togrofbot.database.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@NamedEntityGraph(
   name = "alias-entity",
   attributeNodes = {
       @NamedAttributeNode("id"),
       @NamedAttributeNode("name"),
       @NamedAttributeNode("guildId"),
       @NamedAttributeNode("command")
   }
)
@Entity
@Table(name = "alias")
@Getter
@Setter
@EqualsAndHashCode
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

    @Column(name = "creation_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDatetime;

}
