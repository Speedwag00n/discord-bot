package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.Specification;

public interface PlaylistRepository extends Repository<PlaylistEntity> {

    void addPlaylist(PlaylistEntity entity);

    boolean removePlaylist(PlaylistEntity entity);
    long removePlaylists(Specification<PlaylistEntity> specification);

    int updatePlaylistName(Specification<PlaylistEntity> specification, String name);

}
