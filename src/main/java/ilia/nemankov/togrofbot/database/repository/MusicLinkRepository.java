package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.specification.Specification;

import java.util.List;

public interface MusicLinkRepository {

    void addMusicLink(MusicLinkEntity entity);
    int removeMusicLink(MusicLinkEntity entity);

    long count(Specification specification);
    long count(Specification specification, QuerySettings settings);

    List<MusicLinkEntity> query(Specification specification, String graphName);
    List<MusicLinkEntity> query(Specification specification, String graphName, QuerySettings settings);

}
