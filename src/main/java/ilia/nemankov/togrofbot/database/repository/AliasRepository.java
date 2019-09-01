package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.specification.Specification;

public interface AliasRepository extends Repository<AliasEntity> {

    void addAlias(AliasEntity entity);

    boolean removeAlias(AliasEntity entity);
    long removeAliases(Specification<AliasEntity> specification);

    void updateAlias(AliasEntity entity);

}
