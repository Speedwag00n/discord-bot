package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.PresentationEntity;

public interface PresentationRepository extends Repository<PresentationEntity> {

    void saveOrUpdatePresentation(PresentationEntity entity);

}
