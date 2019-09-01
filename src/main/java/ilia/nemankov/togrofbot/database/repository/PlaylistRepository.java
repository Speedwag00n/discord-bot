package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.Specification;

public interface PlaylistRepository<T> extends Repository<PlaylistEntity> {

    void addPlaylist(PlaylistEntity entity);
    int removePlaylist(PlaylistEntity entity);
    int updatePlaylistName(Specification specification, String name);

}
