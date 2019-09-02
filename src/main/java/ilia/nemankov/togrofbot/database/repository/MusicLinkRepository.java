package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.specification.Specification;

public interface MusicLinkRepository extends Repository<MusicLinkEntity> {

    void addMusicLink(MusicLinkEntity entity);

    boolean removeMusicLink(MusicLinkEntity entity);
    long removeMusicLinks(Specification<MusicLinkEntity> specification);

}
