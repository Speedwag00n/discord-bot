package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;

import java.util.List;

public interface MusicLinkRepository {

    void addMusicLink(MusicLinkEntity entity);
    int removeMusicLink(MusicLinkEntity entity);

    long count(HibernateSpecification specification);
    long count(HibernateSpecification specification, QuerySettings settings);

    List<MusicLinkEntity> query(HibernateSpecification specification);
    List<MusicLinkEntity> query(HibernateSpecification specification, QuerySettings settings);

}
