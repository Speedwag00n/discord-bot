package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.specification.Specification;

import java.util.List;

public interface AliasRepository {

    void addAlias(AliasEntity entity);
    int removeAlias(AliasEntity entity);
    int updateAliasName(Specification specification, String name);

    long count(Specification specification);
    long count(Specification specification, QuerySettings settings);

    List<AliasEntity> query(Specification specification, String graphName);
    List<AliasEntity> query(Specification specification, String graphName, QuerySettings settings);

}
