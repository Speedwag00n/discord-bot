package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.Specification;

import java.util.List;

public interface PlaylistRepository {

    void addPlaylist(PlaylistEntity entity);
    int removePlaylist(PlaylistEntity entity);
    int updatePlaylistName(Specification specification, String name);

    long count(Specification specification);
    long count(Specification specification, QuerySettings settings);

    List<PlaylistEntity> query(Specification specification, String graphName);
    List<PlaylistEntity> query(Specification specification, String graphName, QuerySettings settings);

}
