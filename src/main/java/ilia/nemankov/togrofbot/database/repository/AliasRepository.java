package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.specification.Specification;

public interface AliasRepository extends Repository<AliasEntity> {

    void addAlias(AliasEntity entity);
    int removeAlias(AliasEntity entity);
    int updateAliasName(Specification specification, String name);

}
