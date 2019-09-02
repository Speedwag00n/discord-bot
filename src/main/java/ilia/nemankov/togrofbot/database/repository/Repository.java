package ilia.nemankov.togrofbot.database.repository;

import ilia.nemankov.togrofbot.database.specification.Specification;
import ilia.nemankov.togrofbot.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public interface Repository<T> {

    default long count(Specification<T> specification) {
        return this.count(specification, null);
    }

    default long count(Specification<T> specification, QuerySettings settings) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

        Root<T> root = criteria.from(specification.getType());
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

    default List<T> query(Specification<T> specification, String graphName) {
        return this.query(specification, graphName, null);
    }

    default List<T> query(Specification<T> specification, String graphName, QuerySettings settings) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(specification.getType());

        Root<T> root = criteria.from(specification.getType());
        criteria.select(root).where(specification.getPredicate(builder, root));
        criteria.orderBy(builder.asc(root.get("creationDatetime")));

        TypedQuery<T> query = session.createQuery(criteria);
        query.setHint("javax.persistence.fetchgraph", session.getEntityGraph(graphName));
        if (settings != null) {
            HibernateUtils.applySettings(query, settings);
        }

        try {
            List<T> result = query.getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

}
