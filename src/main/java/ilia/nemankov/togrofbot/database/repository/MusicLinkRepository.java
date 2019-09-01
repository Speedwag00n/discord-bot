package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;

public interface MusicLinkRepository<T> extends Repository<MusicLinkEntity> {

    void addMusicLink(MusicLinkEntity entity);
    int removeMusicLink(MusicLinkEntity entity);

}
