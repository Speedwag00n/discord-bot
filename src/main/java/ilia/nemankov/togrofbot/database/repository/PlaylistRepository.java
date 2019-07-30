package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;

import java.util.List;

public interface PlaylistRepository {

    void addPlaylist(PlaylistEntity account);
    void removePlaylist(PlaylistEntity account);
    void updatePlaylist(PlaylistEntity account);

    List<PlaylistEntity> query(HibernateSpecification specification);

}
