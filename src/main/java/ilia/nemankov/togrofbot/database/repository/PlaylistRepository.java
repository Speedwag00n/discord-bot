package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;

import java.util.List;

public interface PlaylistRepository {

    void addPlaylist(PlaylistEntity entity);
    int removePlaylist(PlaylistEntity entity);
    void updatePlaylist(PlaylistEntity entity);

    List<PlaylistEntity> query(HibernateSpecification specification);

}
