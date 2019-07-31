package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;

import java.util.List;

public interface MusicLinkRepository {

    void addMusicLink(MusicLinkEntity entity);
    void removeMusicLink(MusicLinkEntity entity) throws ItemNotPresentedException;
    void updateMusicLink(MusicLinkEntity entity);

    List<MusicLinkEntity> query(HibernateSpecification specification);

}
