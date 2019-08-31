package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.repository.AliasRepository;
import ilia.nemankov.togrofbot.database.repository.QuerySettings;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;
import ilia.nemankov.togrofbot.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.List;

public class AliasRepositoryImpl implements AliasRepository {

    @Override
    public void addAlias(AliasEntity entity) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public int removeAlias(AliasEntity entity) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("DELETE AliasEntity WHERE name = :paramName and guildId = :paramGuildId");
        query.setParameter("paramName", entity.getName());
        query.setParameter("paramGuildId", entity.getGuildId());

        try {
            int deleted = query.executeUpdate();
            transaction.commit();
            return deleted;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public int updateAliasName(HibernateSpecification specification, String name) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaUpdate<AliasEntity> criteria = builder.createCriteriaUpdate(AliasEntity.class);

        Root<AliasEntity> root = criteria.from(AliasEntity.class);
        criteria.set(root.get("name"), name).where(specification.getPredicate(builder, root));

        TypedQuery<Long> query = session.createQuery(criteria);
        try {
            int updated = query.executeUpdate();
            transaction.commit();
            return updated;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public long count(HibernateSpecification specification) {
        return this.count(specification, null);
    }

    @Override
    public long count(HibernateSpecification specification, QuerySettings settings) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

        Root<AliasEntity> root = criteria.from(AliasEntity.class);
        criteria.select(builder.count(root)).where(specification.getPredicate(builder, root));

        TypedQuery<Long> query = session.createQuery(criteria);
        if (settings != null) {
            HibernateUtils.applySettings(query, settings);
        }

        try {
            long result = query.getSingleResult();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public List<AliasEntity> query(HibernateSpecification specification, String graphName) {
        return this.query(specification, graphName, null);
    }

    @Override
    public List<AliasEntity> query(HibernateSpecification specification, String graphName, QuerySettings settings) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<AliasEntity> criteria = builder.createQuery(AliasEntity.class);

        Root<AliasEntity> root = criteria.from(AliasEntity.class);
        criteria.select(root).where(specification.getPredicate(builder, root));
        criteria.orderBy(builder.asc(root.get("creationDatetime")));

        TypedQuery<AliasEntity> query = session.createQuery(criteria);
        query.setHint("javax.persistence.fetchgraph", session.getEntityGraph(graphName));
        if (settings != null) {
            HibernateUtils.applySettings(query, settings);
        }

        try {
            List<AliasEntity> result = query.getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

}
