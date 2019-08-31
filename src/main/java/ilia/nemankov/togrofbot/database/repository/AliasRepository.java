package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;

import java.util.List;

public interface AliasRepository {

    void addAlias(AliasEntity entity);
    int removeAlias(AliasEntity entity);
    int updateAliasName(HibernateSpecification specification, String name);

    long count(HibernateSpecification specification);
    long count(HibernateSpecification specification, QuerySettings settings);

    List<AliasEntity> query(HibernateSpecification specification, String graphName);
    List<AliasEntity> query(HibernateSpecification specification, String graphName, QuerySettings settings);

}
